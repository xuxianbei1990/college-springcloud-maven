package college.rocketmq.client.impl;

import college.rocket.remoting.RPCHook;
import college.rocket.remoting.RemotingClient;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingClient;
import college.rocketmq.client.ClientConfig;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:40
 * Version:V1.0
 */
public class MQClientAPIImpl {


    private final RemotingClient remotingClient;

    public MQClientAPIImpl(final NettyClientConfig nettyClientConfig,
                           final ClientRemotingProcessor clientRemotingProcessor,
                           RPCHook rpcHook, final ClientConfig clientConfig) {
        remotingClient = new NettyRemotingClient(nettyClientConfig, null);
    }


    public void start() {
        this.remotingClient.start();
    }
}
