package college.rocketmq.client.impl.factory;

import college.rocket.common.ServiceState;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.impl.ClientRemotingProcessor;
import college.rocketmq.client.impl.MQClientAPIImpl;

import static college.rocket.common.ServiceState.CREATE_JUST;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 17:36
 * Version:V1.0
 */
public class MQClientInstance {

    private ServiceState serviceState = CREATE_JUST;

    private final MQClientAPIImpl mQClientAPIImpl;
    private final NettyClientConfig nettyClientConfig;
    private final ClientRemotingProcessor clientRemotingProcessor;

    public MQClientInstance(ClientConfig clientConfig, int instanceIndex, String clientId, RPCHook rpcHook) {
        nettyClientConfig = new NettyClientConfig();
        clientRemotingProcessor = new ClientRemotingProcessor(this);
        mQClientAPIImpl = new MQClientAPIImpl(this.nettyClientConfig, this.clientRemotingProcessor, rpcHook, clientConfig);
    }

    public void start() throws MQClientException {
        synchronized (this) {
            switch (this.serviceState) {
                case CREATE_JUST:
                    this.serviceState = ServiceState.START_FAILED;
                    mQClientAPIImpl.start();
            }
        }
    }

}
