package college.rocketmq.client.impl.producer;

import college.rocket.common.ServiceState;
import college.rocket.remoting.RPCHook;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.impl.MQClientManager;
import college.rocketmq.client.impl.factory.MQClientInstance;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:37
 * Version:V1.0
 */
public class DefaultMQProducerImpl implements MQProducerInner {

    private ServiceState serviceState = ServiceState.CREATE_JUST;
    private final DefaultMQProducer defaultMQProducer;
    private final RPCHook rpcHook;
    private MQClientInstance mQClientFactory;

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
}
