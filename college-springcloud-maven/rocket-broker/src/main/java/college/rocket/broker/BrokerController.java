package college.rocket.broker;

import college.rocket.broker.client.*;
import college.rocket.broker.dledger.DLedgerRoleChangeHandler;
import college.rocket.broker.filtersrv.FilterServerManager;
import college.rocket.broker.latency.BrokerFixedThreadPoolExecutor;
import college.rocket.broker.longpolling.NotifyMessageArrivingListener;
import college.rocket.broker.longpolling.PullRequestHoldService;
import college.rocket.broker.out.BrokerOuterAPI;
import college.rocket.broker.processor.PullMessageProcessor;
import college.rocket.broker.processor.SendMessageProcessor;
import college.rocket.broker.slave.SlaveSynchronize;
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
import college.rocket.store.config.BrokerRole;
import college.rocket.store.config.MessageStoreConfig;
import college.rocket.store.dledger.DLedgerCommitLog;
import college.rocket.store.stats.BrokerStatsManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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

    private final ConsumerManager consumerManager;
    private final SlaveSynchronize slaveSynchronize;
    private final ConsumerIdsChangeListener consumerIdsChangeListener;
    private final ProducerManager producerManager;
    private final ClientHousekeepingService clientHousekeepingService;
    private final FilterServerManager filterServerManager;
    private ExecutorService sendMessageExecutor;
    private TopicConfigManager topicConfigManager;
    private final BlockingQueue<Runnable> sendThreadPoolQueue;
    private final BlockingQueue<Runnable> pullThreadPoolQueue;
    private final PullMessageProcessor pullMessageProcessor;
    private ExecutorService pullMessageExecutor;
    private boolean updateMasterHAServerAddrPeriodically = false;

    private Future<?> slaveSyncFuture;

    //DefaultMessageStore
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
        //客户保持服务：实际就是把长期在线，但是没有数据更新的客户端主动关闭掉
        this.clientHousekeepingService = new ClientHousekeepingService(this);
        this.topicConfigManager = new TopicConfigManager(this);
        // 拉取请求持有服务
        this.pullRequestHoldService = new PullRequestHoldService(this);
        // 通知消息到达监听
        this.messageArrivingListener = new NotifyMessageArrivingListener(this.pullRequestHoldService);
        // 消费id变更监听
        this.consumerIdsChangeListener = new DefaultConsumerIdsChangeListener(this);
        // 消费管理
        this.consumerManager = new ConsumerManager(this.consumerIdsChangeListener);
        //从库同步
        this.slaveSynchronize = new SlaveSynchronize(this);
        //broker 状态管理
        this.brokerStatsManager = new BrokerStatsManager(this.brokerConfig.getBrokerClusterName());
        //接收发送消息线程池队列
        this.sendThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getSendThreadPoolQueueCapacity());
        // 拉取消息处理
        this.pullMessageProcessor = new PullMessageProcessor(this);
        //拉取线程池队列
        this.pullThreadPoolQueue = new LinkedBlockingQueue<Runnable>(this.brokerConfig.getPullThreadPoolQueueCapacity());
        //生产者管理
        this.producerManager = new ProducerManager();
    }

    public boolean initialize() throws CloneNotSupportedException, IOException {
        //主题类加载,从C:\Users\2250\store\config 加载，这里的C:\Users\2250是本机账户
        boolean result = this.topicConfigManager.load();

        if (result) {
            //消息存储
            this.messageStore = new DefaultMessageStore(this.messageStoreConfig, this.brokerStatsManager, this.messageArrivingListener,
                    this.brokerConfig);
            //是否开启了DLeger 主要功能是根据raft协议实现自从选举master。解决master/salve master 挂掉之后，需要手动处理master情况
            if (messageStoreConfig.isEnableDLegerCommitLog()) {
                DLedgerRoleChangeHandler roleChangeHandler = new DLedgerRoleChangeHandler(this, (DefaultMessageStore) messageStore);
                ((DLedgerCommitLog) ((DefaultMessageStore) messageStore).getCommitLog()).getdLedgerServer().getdLedgerLeaderElector().addRoleChangeHandler(roleChangeHandler);
            }

        }

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


        return true;
    }

    private void registerProcessor() {

        SendMessageProcessor sendProcessor = new SendMessageProcessor(this);
        this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE, sendProcessor, this.sendMessageExecutor);
        this.remotingServer.registerProcessor(RequestCode.SEND_MESSAGE_V2, sendProcessor, this.sendMessageExecutor);

    }

    public void start() throws Exception {

        //消息存储
        if (this.messageStore != null) {
            this.messageStore.start();
        }

        //远程服务启动
        if (this.remotingServer != null) {
            this.remotingServer.start();
        }

        //作为客户端连接NameSrv做的配置信息
        if (this.brokerOuterAPI != null) {
            this.brokerOuterAPI.start();
        }

        //Ha 最新版RocketMq是通过Raft协议实现的。
        if (!messageStoreConfig.isEnableDLegerCommitLog()) {
            startProcessorByHa(messageStoreConfig.getBrokerRole());
            //如果是主从，把主的信息同步过来，通过api方式
            handleSlaveSynchronize(messageStoreConfig.getBrokerRole());
            this.registerBrokerAll(true, false, true);
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

    private void handleSlaveSynchronize(BrokerRole role) {
        if (role == BrokerRole.SLAVE) {
            slaveSyncFuture = this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        BrokerController.this.slaveSynchronize.syncAll();
                    } catch (Throwable e) {
                        log.error("ScheduledTask SlaveSynchronize syncAll error.", e);
                    }
                }
            }, 1000 * 3, 1000 * 10, TimeUnit.MILLISECONDS);
        }
    }

    private void startProcessorByHa(BrokerRole role) {
        if (BrokerRole.SLAVE != role) {
//            if (this.transactionalMessageCheckService != null) {
//                this.transactionalMessageCheckService.start();
//            }
        }
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
                if (this.updateMasterHAServerAddrPeriodically && registerBrokerResult.getHaServerAddr() != null) {
                    this.messageStore.updateHaMasterAddress(registerBrokerResult.getHaServerAddr());
                }

                //注册时候可以拿到Master地址，那么应该是NameSrv赋值的
                this.slaveSynchronize.setMasterAddr(registerBrokerResult.getMasterAddr());

                if (checkOrderConfig) {
                    //大概意思是更新TopicConfig信息的
                    this.getTopicConfigManager().updateOrderTopicConfig(registerBrokerResult.getKvTable());
                }
            }
        }

    }

    /**
     * 同一个IP地址，监听端口主要用于Master的Slave同步
     *
     * @return
     */
    public String getHAServerAddr() {
        return this.brokerConfig.getBrokerIP2() + ":" + this.messageStoreConfig.getHaListenPort();
    }
}
