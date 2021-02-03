package college.rocket.broker;

import college.rocket.broker.client.ClientHousekeepingService;
import college.rocket.broker.filtersrv.FilterServerManager;
import college.rocket.broker.latency.BrokerFixedThreadPoolExecutor;
import college.rocket.broker.longpolling.NotifyMessageArrivingListener;
import college.rocket.broker.longpolling.PullRequestHoldService;
import college.rocket.broker.out.BrokerOuterAPI;
import college.rocket.broker.processor.PullMessageProcessor;
import college.rocket.broker.processor.SendMessageProcessor;
import college.rocket.broker.topic.TopicConfigManager;
import college.rocket.common.BrokerConfig;
import college.rocket.common.ThreadFactoryImpl;
import college.rocket.common.namesrv.RegisterBrokerResult;
import college.rocket.common.protocol.RequestCode;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;
import college.rocket.remoting.RemotingServer;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyRemotingServer;
import college.rocket.remoting.netty.NettyServerConfig;
import college.rocket.store.DefaultMessageStore;
import college.rocket.store.MessageArrivingListener;
import college.rocket.store.MessageStore;
import college.rocket.store.config.MessageStoreConfig;
import college.rocket.store.stats.BrokerStatsManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.*;

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
    private final BrokerStatsManager brokerStatsManager;
    private final MessageArrivingListener messageArrivingListener;
    private final PullRequestHoldService pullRequestHoldService;
    private final BrokerOuterAPI brokerOuterAPI;
    private final ClientHousekeepingService clientHousekeepingService;
    private final FilterServerManager filterServerManager;
    private ExecutorService sendMessageExecutor;
    private TopicConfigManager topicConfigManager;
    private final BlockingQueue<Runnable> sendThreadPoolQueue;
    private final BlockingQueue<Runnable> pullThreadPoolQueue;
    private final PullMessageProcessor pullMessageProcessor;
    private ExecutorService pullMessageExecutor;

    private MessageStore messageStore;

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
        // 拉取请求持有服务
        this.pullRequestHoldService = new PullRequestHoldService(this);
        // 通知消息到达监听
        this.messageArrivingListener = new NotifyMessageArrivingListener(this.pullRequestHoldService);
        //broker 状态管理
        this.brokerStatsManager = new BrokerStatsManager(this.brokerConfig.getBrokerClusterName());
        //接收发送消息线程池队列
        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getSendThreadPoolQueueCapacity());
        // 拉取消息处理
        this.pullMessageProcessor = new PullMessageProcessor(this);
        //拉取线程池队列
        this.pullThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getPullThreadPoolQueueCapacity());
    }

    public boolean initialize() throws CloneNotSupportedException {
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.clientHousekeepingService);
        if (this.brokerConfig.getNamesrvAddr() != null) {
            this.brokerOuterAPI.updateNameServerAddressList(this.brokerConfig.getNamesrvAddr());
        }

        //处理发送消息的线程池, 超过队列直接报错
        this.sendMessageExecutor = new BrokerFixedThreadPoolExecutor(
                this.brokerConfig.getSendMessageThreadPoolNums(),
                this.brokerConfig.getSendMessageThreadPoolNums(),
                1000 * 60,
                TimeUnit.MILLISECONDS,
                this.sendThreadPoolQueue,
                new ThreadFactoryImpl("SendMessageThread_"));


        //拉取消息的线程池
        this.pullMessageExecutor = new BrokerFixedThreadPoolExecutor(
                this.brokerConfig.getPullMessageThreadPoolNums(),
                this.brokerConfig.getPullMessageThreadPoolNums(),
                1000 * 60,
                TimeUnit.MILLISECONDS,
                this.pullThreadPoolQueue,
                new ThreadFactoryImpl("PullMessageThread_"));

        this.registerProcessor();
        //消息存储
        this.messageStore = new DefaultMessageStore(this.messageStoreConfig, this.brokerStatsManager, this.messageArrivingListener,
                this.brokerConfig);

        return true;
    }

    private void registerProcessor() {

        SendMessageProcessor sendProcessor = new SendMessageProcessor(this);
        this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE, sendProcessor, this.sendMessageExecutor);
        this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE_V2, sendProcessor, this.sendMessageExecutor);

    }

    public void start() {

        //远程服务启动
        if (this.remotingServer != null) {
            this.remotingServer.start();
        }

        //作为客户端连接NameSrv做的配置信息
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
