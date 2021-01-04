package college.rocket.remoting.netty;

import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:47
 * Version:V1.0
 */
public class AsyncNettyRequestProcessor implements NettyRequestProcessor {
    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
