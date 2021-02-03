package college.rocket.broker.processor;

import college.rocket.broker.BrokerController;
import college.rocket.broker.longpolling.PullRequest;
import college.rocket.common.protocol.ResponseCode;
import college.rocket.common.protocol.header.PullMessageRequestHeader;
import college.rocket.common.protocol.header.PullMessageResponseHeader;
import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.netty.AsyncNettyRequestProcessor;
import college.rocket.remoting.netty.NettyRequestProcessor;
import college.rocket.remoting.netty.RequestTask;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocket.store.MessageFilter;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 13:58
 * Version:V1.0
 */
@Slf4j
public class PullMessageProcessor extends AsyncNettyRequestProcessor implements NettyRequestProcessor {
    private final BrokerController brokerController;

    public PullMessageProcessor(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return this.processRequest(ctx.channel(), request, true);
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }

    private RemotingCommand processRequest(final Channel channel, RemotingCommand request, boolean brokerAllowSuspend)
            throws RemotingCommandException {
        RemotingCommand response = RemotingCommand.createResponseCommand(PullMessageResponseHeader.class);
        final PullMessageResponseHeader responseHeader = (PullMessageResponseHeader) response.getCustomHeader();
        final PullMessageRequestHeader requestHeader =
                (PullMessageRequestHeader) request.decodeCommandCustomHeader(PullMessageRequestHeader.class);
        response.setOpaque(request.getOpaque());

        boolean hasSuspendFlag = false;

        final long suspendTimeoutMillisLong = hasSuspendFlag ? requestHeader.getSuspendTimeoutMillis() : 0;

        SubscriptionData subscriptionData = null;

        MessageFilter messageFilter = null;
        switch (response.getCode()) {
            case ResponseCode.PULL_NOT_FOUND:
                if (brokerAllowSuspend && hasSuspendFlag) {
                    long pollingTimeMills = suspendTimeoutMillisLong;

                    String topic = requestHeader.getTopic();
                    long offset = requestHeader.getQueueOffset();
                    int queueId = requestHeader.getQueueId();
                    PullRequest pullRequest = new PullRequest(request, channel, pollingTimeMills,
                            System.currentTimeMillis(), offset, subscriptionData, messageFilter);
                    this.brokerController.getPullRequestHoldService().suspendPullRequest(topic, queueId, pullRequest);
                    response = null;
                    break;
                }
        }
        return response;
    }

    public void executeRequestWhenWakeup(final Channel channel,
                                         final RemotingCommand request) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    final RemotingCommand response = PullMessageProcessor.this.processRequest(channel, request, false);

                    if (response != null) {
                        response.setOpaque(request.getOpaque());
                        response.markResponseType();
                        try {
                            channel.writeAndFlush(response).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    if (!future.isSuccess()) {
                                        log.error("processRequestWrapper response to {} failed",
                                                future.channel().remoteAddress(), future.cause());
                                        log.error(request.toString());
                                        log.error(response.toString());
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            log.error("processRequestWrapper process request over, but response failed", e);
                            log.error(request.toString());
                            log.error(response.toString());
                        }
                    }
                } catch (RemotingCommandException e1) {
                    log.error("excuteRequestWhenWakeup run", e1);
                }
            }
        };
        this.brokerController.getPullMessageExecutor().submit(new RequestTask(run, channel, request));
    }
}
