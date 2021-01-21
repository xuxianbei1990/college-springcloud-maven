package college.rocket.broker.processor;

import college.rocket.broker.BrokerController;
import college.rocket.common.TopicConfig;
import college.rocket.common.message.MessageConst;
import college.rocket.common.protocol.header.SendMessageRequestHeader;
import college.rocket.common.protocol.header.SendMessageResponseHeader;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.netty.NettyRequestProcessor;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocket.store.MessageExtBrokerInner;
import college.rocket.store.PutMessageResult;
import college.rocketmq.client.hook.SendMessageContext;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author: xuxianbei
 * Date: 2021/1/21
 * Time: 10:11
 * Version:V1.0
 */
@Slf4j
public class SendMessageProcessor extends AbstractSendMessageProcessor implements NettyRequestProcessor {


    public SendMessageProcessor(BrokerController brokerController) {
        super(brokerController);
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        RemotingCommand response = null;
        try {
            response = asyncProcessRequest(ctx, request).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("process SendMessage error, request : " + request.toString(), e);
        }
        return response;
    }

    public CompletableFuture<RemotingCommand> asyncProcessRequest(ChannelHandlerContext ctx,
                                                                  RemotingCommand request) throws RemotingCommandException {
        final SendMessageContext mqtraceContext = null;
        switch (request.getCode()) {
            default:
                SendMessageRequestHeader requestHeader = parseRequestHeader(request);
                if (requestHeader == null) {
                    return CompletableFuture.completedFuture(null);
                } else {
                    return this.asyncSendMessage(ctx, request, mqtraceContext, requestHeader);
                }
        }
    }

    private CompletableFuture<RemotingCommand> asyncSendMessage(ChannelHandlerContext ctx, RemotingCommand request,
                                                                SendMessageContext mqtraceContext,
                                                                SendMessageRequestHeader requestHeader) {
        final RemotingCommand response = preSend(ctx, request, requestHeader);
        final SendMessageResponseHeader responseHeader = (SendMessageResponseHeader)response.getCustomHeader();
        //这个就是消息内容
        final byte[] body = request.getBody();
        //获取请求总队列ID
        int queueIdInt = requestHeader.getQueueId();
        TopicConfig topicConfig = this.brokerController.getTopicConfigManager().selectTopicConfig(requestHeader.getTopic());

        MessageExtBrokerInner msgInner = new MessageExtBrokerInner();
        msgInner.setTopic(requestHeader.getTopic());
        msgInner.setQueueId(queueIdInt);
        msgInner.setBody(body);
        msgInner.setPropertiesString(requestHeader.getProperties());
        msgInner.setBornHost(ctx.channel().remoteAddress());
        msgInner.setStoreHost(this.getStoreHost());
        msgInner.setReconsumeTimes(requestHeader.getReconsumeTimes() == null ? 0 : requestHeader.getReconsumeTimes());
        String clusterName = this.brokerController.getBrokerConfig().getBrokerClusterName();
        msgInner.getProperties().put(MessageConst.PROPERTY_CLUSTER, clusterName);
        CompletableFuture<PutMessageResult> putMessageResult = null;
        putMessageResult = this.brokerController.getMessageStore().asyncPutMessage(msgInner);

        return null;
    }

    private RemotingCommand preSend(ChannelHandlerContext ctx, RemotingCommand request,
                                    SendMessageRequestHeader requestHeader) {
        final RemotingCommand response = RemotingCommand.createResponseCommand(SendMessageResponseHeader.class);
        response.setOpaque(request.getOpaque());
        response.setCode(-1);
        return response;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
