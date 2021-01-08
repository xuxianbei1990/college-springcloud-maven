package college.rocket.remoting.netty;

import college.rocket.remoting.ChannelEventListener;
import college.rocket.remoting.common.Pair;
import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.common.ServiceThread;
import college.rocket.remoting.protocol.RemotingCommand;
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
