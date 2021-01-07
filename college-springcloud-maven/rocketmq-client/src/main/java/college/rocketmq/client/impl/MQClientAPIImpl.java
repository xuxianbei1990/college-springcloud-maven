package college.rocketmq.client.impl;

import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.ResponseCode;
import college.rocket.common.protocol.header.namesrv.GetRouteInfoRequestHeader;
import college.rocket.common.protocol.route.TopicRouteData;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.RemotingClient;
import college.rocket.remoting.exception.RemotingConnectException;
import college.rocket.remoting.exception.RemotingException;
import college.rocket.remoting.exception.RemotingSendRequestException;
import college.rocket.remoting.exception.RemotingTimeoutException;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingClient;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:40
 * Version:V1.0
 */
@Slf4j
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

    public TopicRouteData getTopicRouteInfoFromNameServer(final String topic, final long timeoutMillis)
            throws RemotingException, MQClientException, InterruptedException {

        return getTopicRouteInfoFromNameServer(topic, timeoutMillis, true);
    }

    public TopicRouteData getTopicRouteInfoFromNameServer(final String topic, final long timeoutMillis,
                                                          boolean allowTopicNotExist)
            throws MQClientException, InterruptedException, RemotingTimeoutException, RemotingSendRequestException, RemotingConnectException {
        GetRouteInfoRequestHeader requestHeader = new GetRouteInfoRequestHeader();
        requestHeader.setTopic(topic);
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.GET_ROUTEINFO_BY_TOPIC, requestHeader);
        RemotingCommand response = this.remotingClient.invokeSync(null, request, timeoutMillis);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.TOPIC_NOT_EXIST: {
                if (allowTopicNotExist) {
                    log.warn("get Topic [{}] RouteInfoFromNameServer is not exist value", topic);
                }
                break;
            }
            case ResponseCode.SUCCESS: {
                byte[] body = response.getBody();
                if (body != null) {
                    return TopicRouteData.decode(body, TopicRouteData.class);
                }
            }
            default:
                break;
        }
        throw new MQClientException(response.getCode(), response.getRemark());
    }

    public void updateNameServerAddressList(final String addrs) {
        String[] addrArray = addrs.split(";");
        List<String> list = Arrays.asList(addrArray);
        this.remotingClient.updateNameServerAddressList(list);
    }
}
