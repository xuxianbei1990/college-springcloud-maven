package college.rocket.remoting.netty;

import college.rocket.remoting.ChannelEventListener;
import college.rocket.remoting.InvokeCallback;
import college.rocket.remoting.common.Pair;
import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.common.SemaphoreReleaseOnlyOnce;
import college.rocket.remoting.common.ServiceThread;
import college.rocket.remoting.exception.RemotingSendRequestException;
import college.rocket.remoting.exception.RemotingTimeoutException;
import college.rocket.remoting.exception.RemotingTooMuchRequestException;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocket.remoting.protocol.RemotingSysResponseCode;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:57
 * Version:V1.0
 */
@Slf4j
public abstract class NettyRemotingAbstract {

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();
    protected final Semaphore semaphoreOneway;
    protected final Semaphore semaphoreAsync;

    protected Pair<NettyRequestProcessor, ExecutorService> defaultRequestProcessor;

    public abstract ChannelEventListener getChannelEventListener();

    protected final ConcurrentMap<Integer /* opaque */, ResponseFuture> responseTable =
            new ConcurrentHashMap(256);

    protected final HashMap<Integer/* request code */, Pair<NettyRequestProcessor, ExecutorService>> processorTable =
            new HashMap<Integer, Pair<NettyRequestProcessor, ExecutorService>>(64);

    public NettyRemotingAbstract(final int permitsOneway, final int permitsAsync) {
        this.semaphoreOneway = new Semaphore(permitsOneway, true);
        this.semaphoreAsync = new Semaphore(permitsAsync, true);
    }

    public void putNettyEvent(final NettyEvent event) {
        this.nettyEventExecutor.putNettyEvent(event);
    }

    public void invokeAsyncImpl(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                                final InvokeCallback invokeCallback)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException {
        long beginStartTime = System.currentTimeMillis();
        final int opaque = request.getOpaque();
        boolean acquired = this.semaphoreAsync.tryAcquire(timeoutMillis, TimeUnit.MILLISECONDS);

        if (acquired) {
            final SemaphoreReleaseOnlyOnce once = new SemaphoreReleaseOnlyOnce(this.semaphoreAsync);
            long costTime = System.currentTimeMillis() - beginStartTime;
            if (timeoutMillis < costTime) {
                once.release();
                throw new RemotingTimeoutException("invokeAsyncImpl call timeout");
            }
            final ResponseFuture responseFuture = new ResponseFuture(channel, opaque, timeoutMillis - costTime, invokeCallback, once);
            this.responseTable.put(opaque, responseFuture);
            try {
                channel.writeAndFlush(request).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture f) throws Exception {
                        if (f.isSuccess()) {
                            responseFuture.setSendRequestOK(true);
                            return;
                        }
                        requestFail(opaque);
                        log.warn("send a request command to channel <{}> failed.", RemotingHelper.parseChannelRemoteAddr(channel));
                    }


                });
            } catch (Exception e) {
                responseFuture.release();
                log.warn("send a request command to channel <" + RemotingHelper.parseChannelRemoteAddr(channel) + "> Exception", e);
                throw new RemotingSendRequestException(RemotingHelper.parseChannelRemoteAddr(channel), e);
            }
        }
    }

    private void requestFail(int opaque) {

    }

    class NettyEventExecutor extends ServiceThread {
        private final int maxSize = 10000;

        private final LinkedBlockingQueue<NettyEvent> eventQueue = new LinkedBlockingQueue(maxSize);

        public void putNettyEvent(final NettyEvent event) {
            if (this.eventQueue.size() <= maxSize) {
                this.eventQueue.add(event);
            } else {
                log.warn("event queue size[{}] enough, so drop this event {}", this.eventQueue.size(), event.toString());
            }
        }

        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }

        @Override
        public void run() {
            final ChannelEventListener listener = NettyRemotingAbstract.this.getChannelEventListener();

            //我就是比较好奇这里为什么不用线程自带的Thread.Interceptor();
            while (!this.isStopped()) {
                try {
                    NettyEvent event = this.eventQueue.poll(3000, TimeUnit.MILLISECONDS);
                    if (event != null && listener != null) {
                        switch (event.getType()) {
                            case IDLE:
                                listener.onChannelIdle(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CLOSE:
                                listener.onChannelClose(event.getRemoteAddr(), event.getChannel());
                                break;
                            case CONNECT:
                                listener.onChannelConnect(event.getRemoteAddr(), event.getChannel());
                                break;
                            case EXCEPTION:
                                listener.onChannelException(event.getRemoteAddr(), event.getChannel());
                                break;
                            default:
                                break;

                        }
                    }
                } catch (Exception e) {
                    log.warn(this.getServiceName() + " service has exception. ", e);
                }
            }
        }

        public void start() {
            this.thread.start();
        }

        public boolean isStopped() {
            return stopped;
        }
    }

    public void processMessageReceived(ChannelHandlerContext ctx, RemotingCommand msg) throws Exception {
        final RemotingCommand cmd = msg;
        if (cmd != null) {
            switch (cmd.getType()) {
                // 建立连接
                case REQUEST_COMMAND:
                    processRequestCommand(ctx, cmd);
                    break;
                //消费
                case RESPONSE_COMMAND:
                    processResponseCommand(ctx, cmd);
                    break;
                default:
                    break;
            }
        }
    }

    protected void processRequestCommand(ChannelHandlerContext ctx, RemotingCommand cmd) {
        final Pair<NettyRequestProcessor, ExecutorService> matched = this.processorTable.get(cmd.getCode());
        final Pair<NettyRequestProcessor, ExecutorService> pair = null == matched ? this.defaultRequestProcessor : matched;
        //其实就是消息ID
        final int opaque = cmd.getOpaque();
        if (pair != null) {
            Runnable run = () -> {
                try {
                    final RemotingResponseCallback callback = (response) -> {
                        if (!cmd.isOnewayRPC()) {
                            if (response != null) {
                                response.setOpaque(opaque);
                                response.markResponseType();
                                try {
                                    ctx.writeAndFlush(response);
                                } catch (Throwable e) {
                                    log.error("process request over, but response failed", e);
                                    log.error(cmd.toString());
                                    log.error(response.toString());
                                }
                            } else {
                            }
                        }
                    };

                    if (pair.getObject1() instanceof AsyncNettyRequestProcessor) {
                        //本身就是异步实现的
                        AsyncNettyRequestProcessor processor = (AsyncNettyRequestProcessor) pair.getObject1();
                        processor.asyncProcessRequest(ctx, cmd, callback);
                    } else {
                        NettyRequestProcessor processor = pair.getObject1();
                        RemotingCommand response = processor.processRequest(ctx, cmd);
                        callback.callback(response);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            };

            //如果拒绝， 就返回系统繁忙
            if (pair.getObject1().rejectRequest()) {
                final RemotingCommand response = RemotingCommand.createResponseCommand(RemotingSysResponseCode.SYSTEM_BUSY,
                        "[REJECTREQUEST]system busy, start flow control for a while");
                response.setOpaque(opaque);
                ctx.writeAndFlush(response);
                return;
            }
            try {
                final RequestTask requestTask = new RequestTask(run, ctx.channel(), cmd);
                pair.getObject2().submit(requestTask);
            } catch (RejectedExecutionException e) {

            }
        } else {
            String error = " request type " + cmd.getCode() + " not supported";
            final RemotingCommand response =
                    RemotingCommand.createResponseCommand(RemotingSysResponseCode.REQUEST_CODE_NOT_SUPPORTED, error);
            response.setOpaque(opaque);
            ctx.writeAndFlush(response);
            log.error(RemotingHelper.parseChannelRemoteAddr(ctx.channel()) + error);
        }
    }

    public void processResponseCommand(ChannelHandlerContext ctx, RemotingCommand cmd) {
        //从这里拿到一个相应。问题，这里是什么时候写入的
        //如果是启动客户端：在客户端启动时候NettyRemotingClient createChannel 写入的
        // opaque也是这时候创建的本质就是一个自增序号
        final int opaque = cmd.getOpaque();
        final ResponseFuture responseFuture = responseTable.get(opaque);
        if (responseFuture != null) {
            responseFuture.setResponseCommand(cmd);

            responseTable.remove(opaque);

            //如果是客户端启动类这个是null
            if (responseFuture.getInvokeCallback() != null) {
                //执行消费
//                executeInvokeCallback(responseFuture);
            } else {
                //客户端启动走这个
                responseFuture.putResponse(cmd);
                responseFuture.release();
            }
        } else {
            log.warn("receive response, but not matched any request, " + RemotingHelper.parseChannelRemoteAddr(ctx.channel()));
            log.warn(cmd.toString());
        }
    }

}
