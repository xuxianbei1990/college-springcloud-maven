package college.rocketmq.client.impl.producer;

import college.rocket.common.ServiceState;
import college.rocket.common.message.Message;
import college.rocket.common.message.MessageClientIDSetter;
import college.rocket.common.message.MessageDecoder;
import college.rocket.common.message.MessageQueue;
import college.rocket.common.protocol.NamespaceUtil;
import college.rocket.common.protocol.header.SendMessageRequestHeader;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.exception.RemotingException;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;
import college.rocketmq.client.hook.SendMessageContext;
import college.rocketmq.client.impl.CommunicationMode;
import college.rocketmq.client.impl.MQClientManager;
import college.rocketmq.client.impl.factory.MQClientInstance;
import college.rocketmq.client.latency.MQFaultStrategy;
import college.rocketmq.client.producer.DefaultMQProducer;
import college.rocketmq.client.producer.SendCallback;
import college.rocketmq.client.producer.SendResult;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:37
 * Version:V1.0
 */
public class DefaultMQProducerImpl implements MQProducerInner {

    private ServiceState serviceState = ServiceState.CREATE_JUST;
    private MQFaultStrategy mqFaultStrategy = new MQFaultStrategy();
    private final DefaultMQProducer defaultMQProducer;
    private final ConcurrentMap<String/* topic */, TopicPublishInfo> topicPublishInfoTable =
            new ConcurrentHashMap<String, TopicPublishInfo>();
    private final RPCHook rpcHook;
    private MQClientInstance mQClientFactory;
    private final Random random = new Random();

    public DefaultMQProducerImpl(final DefaultMQProducer defaultMQProducer, RPCHook rpcHook) {
        this.defaultMQProducer = defaultMQProducer;
        this.rpcHook = rpcHook;
    }

    public void start() throws MQClientException {
        this.start(true);
    }

    public void start(final boolean startFactory) throws MQClientException {
        switch (this.serviceState) {
            case CREATE_JUST:
                this.serviceState = ServiceState.START_FAILED;
                this.mQClientFactory = MQClientManager.getInstance().getOrCreateMQClientInstance(this.defaultMQProducer, rpcHook);
                boolean registerOK = mQClientFactory.registerProducer(this.defaultMQProducer.getProducerGroup(), this);
                if (!registerOK) {
                    this.serviceState = ServiceState.CREATE_JUST;
                    throw new MQClientException("The producer group[" + this.defaultMQProducer.getProducerGroup()
                            + "] has been created before, specify another name please." /*+ FAQUrl.suggestTodo(FAQUrl.GROUP_NAME_DUPLICATE_URL)*/,
                            null);
                }

//                this.topicPublishInfoTable.put(this.defaultMQProducer.getCreateTopicKey(), new TopicPublishInfo());
                if (startFactory) {
                    mQClientFactory.start();
                }
        }
    }

    public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return send(msg, this.defaultMQProducer.getSendMsgTimeout());
    }

    public SendResult send(Message msg,
                           long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        return this.sendDefaultImpl(msg, CommunicationMode.SYNC, null, timeout);
    }

    private SendResult sendDefaultImpl(
            Message msg,
            final CommunicationMode communicationMode,
            final SendCallback sendCallback,
            final long timeout
    ) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        //确保服务正常运行
        this.makeSureStateOK();
        final long invokeID = random.nextLong();
        long beginTimestampFirst = System.currentTimeMillis();
        long beginTimestampPrev = beginTimestampFirst;
        long endTimestamp;
        TopicPublishInfo topicPublishInfo = this.tryToFindTopicPublishInfo(msg.getTopic());
        //确定主题有消息队列, 没有消费者，也是可以向这个topic发送消息的
        if (topicPublishInfo != null && topicPublishInfo.ok()) {
            SendResult sendResult = null;
            boolean callTimeout = false;
            MessageQueue mq = null;
            //如果是同步，发送失败重试3，
            int timesTotal = communicationMode == CommunicationMode.SYNC ? 1 + this.defaultMQProducer.getRetryTimesWhenSendFailed() : 1;
            int times = 0;
            String[] brokersSent = new String[timesTotal];
            for (; times < timesTotal; times++) {
                String lastBrokerName = null == mq ? null : mq.getBrokerName();
                //选择一个队列发送消息
                MessageQueue mqSelected = this.selectOneMessageQueue(topicPublishInfo, lastBrokerName);
                if (mqSelected != null) {
                    mq = mqSelected;
                    brokersSent[times] = mq.getBrokerName();
                    beginTimestampPrev = System.currentTimeMillis();
                    long costTime = beginTimestampPrev - beginTimestampFirst;
                    if (timeout < costTime) {
                        callTimeout = true;
                        break;
                    }
                    //执行发送消息
                    sendResult = this.sendKernelImpl(msg, mq, communicationMode, sendCallback, topicPublishInfo, timeout - costTime);
                    endTimestamp = System.currentTimeMillis();
                }

            }
            if (sendResult != null) {
                return sendResult;
            }
        }
        throw new MQClientException("No route info of this topic: " + msg.getTopic(), null);
    }

    private SendResult sendKernelImpl(final Message msg,
                                      final MessageQueue mq,
                                      final CommunicationMode communicationMode,
                                      final SendCallback sendCallback,
                                      final TopicPublishInfo topicPublishInfo,
                                      final long timeout) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        long beginStartTime = System.currentTimeMillis();
        String brokerAddr = this.mQClientFactory.findBrokerAddressInPublish(mq.getBrokerName());

        SendMessageContext context = null;
        if (brokerAddr != null) {
            byte[] prevBody = msg.getBody();
            try {
                MessageClientIDSetter.setUniqID(msg);
                boolean topicWithNamespace = false;
                if (null != this.mQClientFactory.getClientConfig().getNamespace()) {
                    msg.setInstanceId(this.mQClientFactory.getClientConfig().getNamespace());
                    topicWithNamespace = true;
                }

                //设置请求头
                SendMessageRequestHeader requestHeader = createRequestHeader(msg, mq);
                SendResult sendResult = null;
                switch (communicationMode) {
                    case ASYNC:
                        Message tmpMessage = msg;
                        long costTimeAsync = System.currentTimeMillis() - beginStartTime;
                        sendResult = this.mQClientFactory.getMQClientAPIImpl().sendMessage(
                                brokerAddr,
                                mq.getBrokerName(),
                                tmpMessage,
                                requestHeader,
                                timeout - costTimeAsync,
                                communicationMode,
                                sendCallback,
                                topicPublishInfo,
                                this.mQClientFactory,
                                this.defaultMQProducer.getRetryTimesWhenSendAsyncFailed(),
                                context,
                                this);
                        break;
                    case SYNC:
                        long costTimeSync = System.currentTimeMillis() - beginStartTime;
                        if (timeout < costTimeSync) {
                            throw new RuntimeException("sendKernelImpl call timeout");
                        }
                        //发送消息
                        sendResult = this.mQClientFactory.getMQClientAPIImpl().sendMessage(
                                brokerAddr,
                                mq.getBrokerName(),
                                msg,
                                requestHeader,
                                timeout - costTimeSync,
                                communicationMode,
                                context,
                                this);
                        break;
                    default:
                        assert false;
                        break;
                }
                return sendResult;
            } catch (Exception e) {
                throw e;
            } finally {
                msg.setBody(prevBody);
                msg.setTopic(NamespaceUtil.withoutNamespace(msg.getTopic(), this.defaultMQProducer.getNamespace()));
            }
        }
        throw new MQClientException("The broker[" + mq.getBrokerName() + "] not exist", null);
    }

    private SendMessageRequestHeader createRequestHeader(Message msg, MessageQueue mq) {
        SendMessageRequestHeader requestHeader = new SendMessageRequestHeader();
        requestHeader.setProducerGroup(this.defaultMQProducer.getProducerGroup());
        requestHeader.setTopic(msg.getTopic());
        requestHeader.setDefaultTopic(this.defaultMQProducer.getCreateTopicKey());
        requestHeader.setDefaultTopicQueueNums(this.defaultMQProducer.getDefaultTopicQueueNums());
        requestHeader.setQueueId(mq.getQueueId());
        requestHeader.setProperties(MessageDecoder.messageProperties2String(msg.getProperties()));
        requestHeader.setReconsumeTimes(0);
        return requestHeader;
    }

    public MessageQueue selectOneMessageQueue(final TopicPublishInfo tpInfo, final String lastBrokerName) {
        return this.mqFaultStrategy.selectOneMessageQueue(tpInfo, lastBrokerName);
    }

    private TopicPublishInfo tryToFindTopicPublishInfo(String topic) {
        TopicPublishInfo topicPublishInfo = this.topicPublishInfoTable.get(topic);
        if (null == topicPublishInfo || !topicPublishInfo.ok()) {

        }
        return null;
    }

    private void makeSureStateOK() throws MQClientException {
        if (this.serviceState != ServiceState.RUNNING) {
            throw new MQClientException("The producer service state not OK, "
                    + this.serviceState, null);
        }
    }
}
