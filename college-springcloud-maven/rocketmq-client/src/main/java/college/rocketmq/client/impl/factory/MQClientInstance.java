package college.rocketmq.client.impl.factory;

import college.rocket.common.MixAll;
import college.rocket.common.ServiceState;
import college.rocket.common.protocol.route.BrokerData;
import college.rocket.common.protocol.route.TopicRouteData;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.exception.RemotingException;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.impl.ClientRemotingProcessor;
import college.rocketmq.client.impl.MQClientAPIImpl;
import college.rocketmq.client.impl.consumer.PullMessageService;
import college.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import college.rocketmq.client.producer.DefaultMQProducer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static college.rocket.common.ServiceState.CREATE_JUST;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 17:36
 * Version:V1.0
 */
@Data
@Slf4j
public class MQClientInstance {

    private ServiceState serviceState = CREATE_JUST;
    private final static long LOCK_TIMEOUT_MILLIS = 3000;
    private final MQClientAPIImpl mQClientAPIImpl;
    private final NettyClientConfig nettyClientConfig;
    private final ClientRemotingProcessor clientRemotingProcessor;
    private final PullMessageService pullMessageService;
    private final DefaultMQProducer defaultMQProducer;
    private final String clientId;
    private final ClientConfig clientConfig;
    private final Lock lockNamesrv = new ReentrantLock();
    private final ConcurrentMap<String/* Topic */, TopicRouteData> topicRouteTable = new ConcurrentHashMap<String, TopicRouteData>();
    private final ConcurrentMap<String/* Broker Name */, HashMap<Long/* brokerId */, String/* address */>> brokerAddrTable =
            new ConcurrentHashMap<String, HashMap<Long, String>>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "MQClientFactoryScheduledThread");
        }
    });

    public MQClientInstance(ClientConfig clientConfig, int instanceIndex, String clientId, RPCHook rpcHook) {
        this.clientConfig = clientConfig;
        nettyClientConfig = new NettyClientConfig();
        clientRemotingProcessor = new ClientRemotingProcessor(this);
        mQClientAPIImpl = new MQClientAPIImpl(this.nettyClientConfig, this.clientRemotingProcessor, rpcHook, clientConfig);
        if (this.clientConfig.getNamesrvAddr() != null) {
            this.mQClientAPIImpl.updateNameServerAddressList(this.clientConfig.getNamesrvAddr());
            log.info("user specified name server address: {}", this.clientConfig.getNamesrvAddr());
        }
        pullMessageService = new PullMessageService(this);
        this.defaultMQProducer = new DefaultMQProducer(MixAll.CLIENT_INNER_PRODUCER_GROUP);
        this.defaultMQProducer.resetClientConfig(clientConfig);
        this.clientId = clientId;
    }

    public void start() throws MQClientException {
        synchronized (this) {
            switch (this.serviceState) {
                case CREATE_JUST:
                    this.serviceState = ServiceState.START_FAILED;
                    //配置netty信息，未启动
                    mQClientAPIImpl.start();
                    //启动周期任务 也是启动client Broker
                    this.startScheduledTask();
                    //只是从队列中拉取消息分发，不是从broker中拉取消息
                    pullMessageService.start();
                    //这里创建了实例，但是没有启动
                    this.defaultMQProducer.getDefaultMQProducerImpl().start(false);
                    this.serviceState = ServiceState.RUNNING;
                    break;
                case START_FAILED:
                    throw new MQClientException("The Factory object[" + this.getClientId() + "] has been created before, and failed.", null);
                default:
                    break;
            }
        }
    }

    private void startScheduledTask() {
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                updateTopicRouteInfoFromNameServer();
            } catch (Exception e) {
                log.error("ScheduledTask updateTopicRouteInfoFromNameServer exception", e);
            }
        }, 10, this.clientConfig.getPollNameServerInterval(), TimeUnit.MILLISECONDS);

    }

    public boolean registerProducer(final String group, final DefaultMQProducerImpl producer) {
        if (null == group || null == producer) {
            return false;
        }

//        MQProducerInner prev = this.producerTable.putIfAbsent(group, producer);
//        if (prev != null) {
//            log.warn("the producer group[{}] exist already.", group);
//            return false;
//        }

        return true;
    }

    public void updateTopicRouteInfoFromNameServer() {
        Set<String> topicList = new HashSet<String>();
        this.updateTopicRouteInfoFromNameServer("TBW102");
    }

    public boolean updateTopicRouteInfoFromNameServer(final String topic) {
        return updateTopicRouteInfoFromNameServer(topic, false, null);
    }

    public boolean updateTopicRouteInfoFromNameServer(final String topic, boolean isDefault,
                                                      DefaultMQProducer defaultMQProducer) {
        try {
            if (this.lockNamesrv.tryLock(LOCK_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                try {
                    TopicRouteData topicRouteData;
                    topicRouteData = this.mQClientAPIImpl.getTopicRouteInfoFromNameServer(topic, 1000 * 3);
                    if (topicRouteData != null) {
//                        for (QueueData data : topicRouteData.getQueueDatas()) {
//                            int queueNums = Math.min(defaultMQProducer.getDefaultTopicQueueNums(), data.getReadQueueNums());
//                            data.setReadQueueNums(queueNums);
//                            data.setWriteQueueNums(queueNums);
//                        }
                        TopicRouteData old = this.topicRouteTable.get(topic);
                        boolean changed = topicRouteDataIsChange(old, topicRouteData);
                        if (!changed) {
//                            changed = this.isNeedUpdateTopicRouteInfo(topic);
                        } else {
                            log.info("the topic[{}] route info changed, old[{}] ,new[{}]", topic, old, topicRouteData);
                        }
                        if (changed) {
                            TopicRouteData cloneTopicRouteData = topicRouteData.cloneTopicRouteData();

                            for (BrokerData bd : topicRouteData.getBrokerDatas()) {
                                this.brokerAddrTable.put(bd.getBrokerName(), bd.getBrokerAddrs());
                            }
                            log.info("topicRouteTable.put. Topic = {}, TopicRouteData[{}]", topic, cloneTopicRouteData);
                            this.topicRouteTable.put(topic, cloneTopicRouteData);
                            return true;
                        }
                    } else {
                        log.warn("updateTopicRouteInfoFromNameServer, getTopicRouteInfoFromNameServer return null, Topic: {}", topic);
                    }
                } catch (MQClientException e) {
                    if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                        log.warn("updateTopicRouteInfoFromNameServer Exception", e);
                    }
                } catch (RemotingException e) {
                    log.error("updateTopicRouteInfoFromNameServer Exception", e);
                    throw new IllegalStateException(e);
                } finally {
                    this.lockNamesrv.unlock();
                }
            } else {
                log.warn("updateTopicRouteInfoFromNameServer tryLock timeout {}ms", LOCK_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
            log.warn("updateTopicRouteInfoFromNameServer Exception", e);
        }
        return false;
    }

    private boolean topicRouteDataIsChange(TopicRouteData olddata, TopicRouteData nowdata) {
//        if (olddata == null || nowdata == null)
        return true;

    }

    public String findBrokerAddressInPublish(String brokerName) {
        HashMap<Long/* brokerId */, String/* address */> map = this.brokerAddrTable.get(brokerName);
        if (map != null && !map.isEmpty()) {
            return map.get(MixAll.MASTER_ID);
        }

        return null;
    }
}
