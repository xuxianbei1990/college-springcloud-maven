package college.rocket.remoting.netty;

import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:48
 * Version:V1.0
 */
public interface NettyRequestProcessor {

    RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request)
            throws Exception;

    boolean rejectRequest();
}
