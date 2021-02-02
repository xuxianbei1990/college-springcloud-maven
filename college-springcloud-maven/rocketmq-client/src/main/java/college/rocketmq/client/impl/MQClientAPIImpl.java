package college.rocketmq.client.impl;

import college.rocket.common.message.Message;
import college.rocket.common.message.MessageClientIDSetter;
import college.rocket.common.message.MessageQueue;
import college.rocket.common.protocol.NamespaceUtil;
import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.ResponseCode;
import college.rocket.common.protocol.header.PullMessageRequestHeader;
import college.rocket.common.protocol.header.SendMessageRequestHeader;
import college.rocket.common.protocol.header.SendMessageRequestHeaderV2;
import college.rocket.common.protocol.header.SendMessageResponseHeader;
import college.rocket.common.protocol.header.namesrv.GetRouteInfoRequestHeader;
import college.rocket.common.protocol.route.TopicRouteData;
import college.rocket.remoting.InvokeCallback;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.RemotingClient;
import college.rocket.remoting.exception.*;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingClient;
import college.rocket.remoting.netty.ResponseFuture;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.PullResult;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;
import college.rocketmq.client.hook.SendMessageContext;
import college.rocketmq.client.impl.consumer.PullCallback;
import college.rocketmq.client.impl.factory.MQClientInstance;
import college.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import college.rocketmq.client.impl.producer.TopicPublishInfo;
import college.rocketmq.client.producer.SendCallback;
import college.rocketmq.client.producer.SendResult;
import college.rocketmq.client.producer.SendStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:40
 * Version:V1.0
 */
@Slf4j
public class MQClientAPIImpl {


    private final RemotingClient remotingClient;

    private ClientConfig clientConfig;

    public MQClientAPIImpl(final NettyClientConfig nettyClientConfig,
                           final ClientRemotingProcessor clientRemotingProcessor,
                           RPCHook rpcHook, final ClientConfig clientConfig) {
        remotingClient = new NettyRemotingClient(nettyClientConfig, null);
    }


    public void start() {
        this.remotingClient.start();
    }

    public TopicRouteData getTopicRouteInfoFromNameServer(final String topic, final long timeoutMillis)
            throws RemotingException, MQClientException, InterruptedException {

        return getTopicRouteInfoFromNameServer(topic, timeoutMillis, true);
    }

    public TopicRouteData getTopicRouteInfoFromNameServer(final String topic, final long timeoutMillis,
                                                          boolean allowTopicNotExist)
            throws MQClientException, InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        GetRouteInfoRequestHeader requestHeader = new GetRouteInfoRequestHeader();
        requestHeader.setTopic(topic);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_ROUTEINFO_BY_TOPIC, requestHeader);
        RemotingCommand response = this.remotingClient.invokeSync(null, request, timeoutMillis);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.TOPIC_NOT_EXIST: {
                if (allowTopicNotExist) {
                    log.warn("get Topic [{}] RouteInfoFromNameServer is not exist value", topic);
                }
                break;
            }
            case ResponseCode.SUCCESS: {
                byte[] body = response.getBody();
                if (body != null) {
                    return TopicRouteData.decode(body, TopicRouteData.class);
                }
            }
            default:
                break;
        }
        throw new MQClientException(response.getCode(), response.getRemark());
    }

    public void updateNameServerAddressList(final String addrs) {
        String[] addrArray = addrs.split(";");
        List<String> list = Arrays.asList(addrArray);
        this.remotingClient.updateNameServerAddressList(list);
    }

    public SendResult sendMessage(
            final String addr,
            final String brokerName,
            final Message msg,
            final SendMessageRequestHeader requestHeader,
            final long timeoutMillis,
            final CommunicationMode communicationMode,
            final SendCallback sendCallback,
            final TopicPublishInfo topicPublishInfo,
            final MQClientInstance instance,
            final int retryTimesWhenSendFailed,
            final SendMessageContext context,
            final DefaultMQProducerImpl producer
    ) throws RemotingException, MQBrokerException, InterruptedException {
        long beginStartTime = System.currentTimeMillis();
        RemotingCommand request = null;
        SendMessageRequestHeaderV2 requestHeaderV2 = SendMessageRequestHeaderV2.createSendMessageRequestHeaderV2(requestHeader);
        request = RemotingCommand.createRequestCommand(RequestCode.SEND_MESSAGE_V2, requestHeaderV2);
        switch (communicationMode) {
            case SYNC:
                long costTimeSync = System.currentTimeMillis() - beginStartTime;
                if (timeoutMillis < costTimeSync) {
                    throw new RemotingTooMuchRequestException("sendMessage call timeout");
                }
                return this.sendMessageSync(addr, brokerName, msg, timeoutMillis - costTimeSync, request);
        }
        return null;
    }

    public SendResult sendMessage(
            final String addr,
            final String brokerName,
            final Message msg,
            final SendMessageRequestHeader requestHeader,
            final long timeoutMillis,
            final CommunicationMode communicationMode,
            final SendMessageContext context,
            final DefaultMQProducerImpl producer
    ) throws RemotingException, MQBrokerException, InterruptedException{
        return sendMessage(addr, brokerName, msg, requestHeader, timeoutMillis, communicationMode, null, null, null, 0, context, producer);
    }


    private SendResult sendMessageSync(
            final String addr,
            final String brokerName,
            final Message msg,
            final long timeoutMillis,
            final RemotingCommand request
    ) throws RemotingException, MQBrokerException, InterruptedException {
        RemotingCommand response = this.remotingClient.invokeSync(addr, request, timeoutMillis);
        assert response != null;
        return this.processSendResponse(brokerName, msg, response);
    }

    private SendResult processSendResponse(
            final String brokerName,
            final Message msg,
            final RemotingCommand response
    ) throws MQBrokerException, RemotingCommandException {
        SendStatus sendStatus;
        switch (response.getCode()) {
            case ResponseCode.FLUSH_DISK_TIMEOUT: {
                sendStatus = SendStatus.FLUSH_DISK_TIMEOUT;
                break;
            }
//            case ResponseCode.FLUSH_SLAVE_TIMEOUT: {
//                sendStatus = SendStatus.FLUSH_SLAVE_TIMEOUT;
//                break;
//            }
//            case ResponseCode.SLAVE_NOT_AVAILABLE: {
//                sendStatus = SendStatus.SLAVE_NOT_AVAILABLE;
//                break;
//            }
            case ResponseCode.SUCCESS: {
                sendStatus = SendStatus.SEND_OK;
                break;
            }
            default: {
                throw new MQBrokerException(response.getCode(), response.getRemark());
            }
        }

        SendMessageResponseHeader responseHeader =
                (SendMessageResponseHeader) response.decodeCommandCustomHeader(SendMessageResponseHeader.class);

        String topic = msg.getTopic();
        if (!StringUtils.isEmpty(this.clientConfig.getNamespace())) {
            topic = NamespaceUtil.withoutNamespace(topic, this.clientConfig.getNamespace());
        }

        MessageQueue messageQueue = new MessageQueue(topic, brokerName, responseHeader.getQueueId());
        String uniqMsgId = MessageClientIDSetter.getUniqID(msg);

        SendResult sendResult = new SendResult(sendStatus,
                uniqMsgId,
                responseHeader.getMsgId(), messageQueue, responseHeader.getQueueOffset());
        return sendResult;
    }

    public PullResult pullMessage(
            final String addr,
            final PullMessageRequestHeader requestHeader,
            final long timeoutMillis,
            final CommunicationMode communicationMode,
            final PullCallback pullCallback
    ) throws RemotingException, MQBrokerException, InterruptedException {
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.PULL_MESSAGE, requestHeader);

        switch (communicationMode) {
            case ONEWAY:
                assert false;
                return null;
            case ASYNC:
                this.pullMessageAsync(addr, request, timeoutMillis, pullCallback);
                return null;
        }
        return null;
    }

    private void pullMessageAsync(
            final String addr,
            final RemotingCommand request,
            final long timeoutMillis,
            final PullCallback pullCallback
    ) throws RemotingException, InterruptedException {
        this.remotingClient.invokeAsync(addr, request, timeoutMillis, new InvokeCallback() {

            @Override
            public void operationComplete(ResponseFuture responseFuture) {
                RemotingCommand response = responseFuture.getResponseCommand();
                if (response != null) {
                    try {
                        //拼装消费消息
                        PullResult pullResult = MQClientAPIImpl.this.processPullResponse(response);
                        assert pullResult != null;
                        //消费成功处理
                        pullCallback.onSuccess(pullResult);
                    } catch (Exception e) {
                        pullCallback.onException(e);
                    }
                } else {
                    if (!responseFuture.isSendRequestOK()) {
                        pullCallback.onException(new MQClientException("send request failed to " + addr + ". Request: " + request, responseFuture.getCause()));
                    } else if (responseFuture.isTimeout()) {
                        pullCallback.onException(new MQClientException("wait response from " + addr + " timeout :" + responseFuture.getTimeoutMillis() + "ms" + ". Request: " + request,
                                responseFuture.getCause()));
                    } else {
                        pullCallback.onException(new MQClientException("unknown reason. addr: " + addr + ", timeoutMillis: " + timeoutMillis + ". Request: " + request, responseFuture.getCause()));
                    }
                }
            }
        });


    }

    private PullResult processPullResponse(RemotingCommand response) {
        return null;
    }

}
