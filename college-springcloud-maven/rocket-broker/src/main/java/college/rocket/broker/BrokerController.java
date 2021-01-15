package college.rocket.broker;

import college.rocket.broker.client.ClientHousekeepingService;
import college.rocket.broker.filtersrv.FilterServerManager;
import college.rocket.broker.out.BrokerOuterAPI;
import college.rocket.broker.topic.TopicConfigManager;
import college.rocket.common.BrokerConfig;
import college.rocket.common.ThreadFactoryImpl;
import college.rocket.common.namesrv.RegisterBrokerResult;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;
import college.rocket.remoting.RemotingServer;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingServer;
import college.rocket.remoting.netty.NettyServerConfig;
import college.rocket.store.config.MessageStoreConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:26
 * Version:V1.0
 */
@Slf4j
@Data
public class BrokerController {

    private final BrokerConfig brokerConfig;
    private final NettyServerConfig nettyServerConfig;
    private final NettyClientConfig nettyClientConfig;
    private final MessageStoreConfig messageStoreConfig;
    private final BrokerOuterAPI brokerOuterAPI;
    private final ClientHousekeepingService clientHousekeepingService;
    private final FilterServerManager filterServerManager;
    private TopicConfigManager topicConfigManager;

    private RemotingServer remotingServer;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryImpl(
            "BrokerControllerScheduledThread"));

    public BrokerController(BrokerConfig brokerConfig,
                            NettyServerConfig nettyServerConfig,
                            NettyClientConfig nettyClientConfig,
                            MessageStoreConfig messageStoreConfig) {
        this.brokerConfig = brokerConfig;
        this.nettyServerConfig = nettyServerConfig;
        this.nettyClientConfig = nettyClientConfig;
        this.messageStoreConfig = messageStoreConfig;
        this.brokerOuterAPI = new BrokerOuterAPI(nettyClientConfig);
        this.filterServerManager = new FilterServerManager(this);
        this.clientHousekeepingService = new ClientHousekeepingService(this);
        this.topicConfigManager = new TopicConfigManager(this);
    }

    public boolean initialize() throws CloneNotSupportedException {
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.clientHousekeepingService);
        if (this.brokerConfig.getNamesrvAddr() != null) {
            this.brokerOuterAPI.updateNameServerAddressList(this.brokerConfig.getNamesrvAddr());
        }
        return true;
    }

    public void start() {

        //远程服务启动
        if (this.remotingServer != null) {
            this.remotingServer.start();
        }

        if (this.brokerOuterAPI != null) {
            this.brokerOuterAPI.start();
        }

        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    registerBrokerAll(true, false, brokerConfig.isForceRegister());
                } catch (Throwable e) {
                    log.error("registerBrokerAll Exception", e);
                }
            }
        }, 1000 * 10, Math.max(10000, Math.min(brokerConfig.getRegisterNameServerPeriod(), 60000)), TimeUnit.MILLISECONDS);

    }

    public synchronized void registerBrokerAll(final boolean checkOrderConfig, boolean oneway, boolean forceRegister) {
        TopicConfigSerializeWrapper topicConfigWrapper = this.getTopicConfigManager().buildTopicConfigSerializeWrapper();

        doRegisterBrokerAll(checkOrderConfig, oneway, topicConfigWrapper);
    }

    public String getBrokerAddr() {
        return this.brokerConfig.getBrokerIP1() + ":" + this.nettyServerConfig.getListenPort();
    }

    private void doRegisterBrokerAll(boolean checkOrderConfig, boolean oneway,
                                     TopicConfigSerializeWrapper topicConfigWrapper) {
        List<RegisterBrokerResult> registerBrokerResultList = this.brokerOuterAPI.registerBrokerAll(
                this.brokerConfig.getBrokerClusterName(),
                this.getBrokerAddr(),
                this.brokerConfig.getBrokerName(),
                this.brokerConfig.getBrokerId(),
                this.getHAServerAddr(),
                topicConfigWrapper,
                this.filterServerManager.buildNewFilterServerList(),
                oneway,
                this.brokerConfig.getRegisterBrokerTimeoutMills(),
                this.brokerConfig.isCompressedRegister());
        if (registerBrokerResultList.size() > 0) {
            RegisterBrokerResult registerBrokerResult = registerBrokerResultList.get(0);
            if (registerBrokerResult != null) {

            }
        }

    }

    public String getHAServerAddr() {
        return this.brokerConfig.getBrokerIP2() + ":" + this.messageStoreConfig.getHaListenPort();
    }
}
