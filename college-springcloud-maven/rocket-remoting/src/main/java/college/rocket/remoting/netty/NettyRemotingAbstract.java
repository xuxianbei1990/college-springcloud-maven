package college.rocket.remoting.netty;

import college.rocket.remoting.ChannelEventListener;
import college.rocket.remoting.common.ServiceThread;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:57
 * Version:V1.0
 */
public abstract class NettyRemotingAbstract {

    protected final NettyEventExecutor nettyEventExecutor = new NettyEventExecutor();

    public abstract ChannelEventListener getChannelEventListener();

    class NettyEventExecutor extends ServiceThread {

        @Override
        public String getServiceName() {
            return NettyEventExecutor.class.getSimpleName();
        }

        @Override
        public void run() {
            final ChannelEventListener listener = NettyRemotingAbstract.this.getChannelEventListener();
        }

        public void start() {
            this.thread.start();
        }
    }


}
