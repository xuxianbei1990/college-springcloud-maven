package college.rocket.namesrv.processor;

import college.rocket.namesrv.NamesrvController;
import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.netty.AsyncNettyRequestProcessor;
import college.rocket.remoting.netty.NettyRequestProcessor;
import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 17:13
 * Version:V1.0
 */
@Slf4j
public class DefaultRequestProcessor extends AsyncNettyRequestProcessor implements NettyRequestProcessor {

    protected final NamesrvController namesrvController;

    public DefaultRequestProcessor(NamesrvController namesrvController) {
        this.namesrvController = namesrvController;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws RemotingCommandException {

        if (ctx != null) {
            log.debug("receive request, {} {} {}",
                    request.getCode(), RemotingHelper.parseChannelRemoteAddr(ctx.channel()), request);
        }
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
