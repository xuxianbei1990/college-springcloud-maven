package college.rocket.broker.out;

import college.rocket.broker.latency.BrokerFixedThreadPoolExecutor;
import college.rocket.common.ThreadFactoryImpl;
import college.rocket.common.UtilAll;
import college.rocket.common.namesrv.RegisterBrokerResult;
import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.ResponseCode;
import college.rocket.common.protocol.body.KVTable;
import college.rocket.common.protocol.body.RegisterBrokerBody;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;
import college.rocket.common.protocol.namesrv.RegisterBrokerRequestHeader;
import college.rocket.common.protocol.namesrv.RegisterBrokerResponseHeader;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.RemotingClient;
import college.rocket.remoting.exception.RemotingCommandException;
import college.rocket.remoting.exception.RemotingConnectException;
import college.rocket.remoting.exception.RemotingSendRequestException;
import college.rocket.remoting.exception.RemotingTimeoutException;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingClient;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocketmq.client.exception.MQBrokerException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:59
 * Version:V1.0
 */
@Slf4j
public class BrokerOuterAPI {

    private final RemotingClient remotingClient;

    private BrokerFixedThreadPoolExecutor brokerOuterExecutor = new BrokerFixedThreadPoolExecutor(4, 10, 1, TimeUnit.MINUTES,
            new ArrayBlockingQueue<Runnable>(32), new ThreadFactoryImpl("brokerOutApi_thread_", true));

    public BrokerOuterAPI(final NettyClientConfig nettyClientConfig) {
        this(nettyClientConfig, null);
    }

    public BrokerOuterAPI(final NettyClientConfig nettyClientConfig, RPCHook rpcHook) {
        this.remotingClient = new NettyRemotingClient(nettyClientConfig);
//        this.remotingClient.registerRPCHook(rpcHook);
    }

    public List<RegisterBrokerResult> registerBrokerAll(final String clusterName,
                                                        final String brokerAddr,
                                                        final String brokerName,
                                                        final long brokerId,
                                                        final String haServerAddr,
                                                        final TopicConfigSerializeWrapper topicConfigWrapper,
                                                        final List<String> filterServerList,
                                                        final boolean oneway,
                                                        final int timeoutMills,
                                                        final boolean compressed) {
        final List<RegisterBrokerResult> registerBrokerResultList = new ArrayList<>();
        //拿到nameSrv集群地址
        List<String> nameServerAddressList = this.remotingClient.getNameServerAddressList();
        if (nameServerAddressList != null && nameServerAddressList.size() > 0) {
            //组织注册请求
            final RegisterBrokerRequestHeader requestHeader = new RegisterBrokerRequestHeader();
            requestHeader.setBrokerAddr(brokerAddr);
            requestHeader.setBrokerId(brokerId);
            requestHeader.setBrokerName(brokerName);
            requestHeader.setClusterName(clusterName);

            RegisterBrokerBody requestBody = new RegisterBrokerBody();
            requestBody.setTopicConfigSerializeWrapper(topicConfigWrapper);

            final byte[] body = requestBody.encode(compressed);
            final int bodyCrc32 = UtilAll.crc32(body);
            //crc32 位校验
            requestHeader.setBodyCrc32(bodyCrc32);

            final CountDownLatch countDownLatch = new CountDownLatch(nameServerAddressList.size());
            for (final String namesrvAddr : nameServerAddressList) {
                brokerOuterExecutor.execute(() -> {
                    try {
                        RegisterBrokerResult result = registerBroker(namesrvAddr, oneway, timeoutMills, requestHeader, body);
                        if (result != null) {
                            registerBrokerResultList.add(result);
                        }

                        log.info("register broker[{}]to name server {} OK", brokerId, namesrvAddr);
                    } catch (Exception e) {
                        log.warn("registerBroker Exception, {}", namesrvAddr, e);
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }

            //这里使用countDownLatch这种方式来实现超时等待，好处嘛就是无视了RPC，这个方法确实可以的
            try {
                countDownLatch.await(timeoutMills, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
            }
        }
        return registerBrokerResultList;
    }

    private RegisterBrokerResult registerBroker(
            final String namesrvAddr,
            final boolean oneway,
            final int timeoutMills,
            final RegisterBrokerRequestHeader requestHeader,
            final byte[] body
    ) throws RemotingCommandException, MQBrokerException, RemotingConnectException, RemotingSendRequestException, RemotingTimeoutException,
            InterruptedException {
        RemotingCommand request = RemotingCommand.createRequestCommand(RequestCode.REGISTER_BROKER, requestHeader);
        request.setBody(body);

        RemotingCommand response = this.remotingClient.invokeSync(namesrvAddr, request, timeoutMills);
        assert response != null;
        switch (response.getCode()) {
            case ResponseCode.SUCCESS: {
                RegisterBrokerResponseHeader responseHeader =
                        (RegisterBrokerResponseHeader) response.decodeCommandCustomHeader(RegisterBrokerResponseHeader.class);
                RegisterBrokerResult result = new RegisterBrokerResult();
                result.setMasterAddr(responseHeader.getMasterAddr());
//                result.setHaServerAddr(responseHeader.getHaServerAddr());
                if (response.getBody() != null) {
                    result.setKvTable(KVTable.decode(response.getBody(), KVTable.class));
                }
                return result;
            }
            default:
                break;
        }

        throw new MQBrokerException(response.getCode(), response.getRemark());
    }

    public void updateNameServerAddressList(final String addrs) {
        List<String> lst = new ArrayList();
        String[] addrArray = addrs.split(";");
        for (String addr : addrArray) {
            lst.add(addr);
        }

        this.remotingClient.updateNameServerAddressList(lst);
    }

    public void start() {
        this.remotingClient.start();
    }
}
