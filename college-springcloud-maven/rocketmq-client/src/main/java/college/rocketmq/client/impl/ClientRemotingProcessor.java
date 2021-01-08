package college.rocketmq.client.impl;

import college.rocket.remoting.netty.AsyncNettyRequestProcessor;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocketmq.client.impl.factory.MQClientInstance;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:46
 * Version:V1.0
 */
public class ClientRemotingProcessor extends AsyncNettyRequestProcessor {

    private final MQClientInstance mqClientFactory;

    public ClientRemotingProcessor(final MQClientInstance mqClientFactory) {
        this.mqClientFactory = mqClientFactory;
    }

    @Override
    public RemotingCommand processRequest(ChannelHandlerContext ctx, RemotingCommand request) throws Exception {
        return null;
    }

    @Override
    public boolean rejectRequest() {
        return false;
    }
}
