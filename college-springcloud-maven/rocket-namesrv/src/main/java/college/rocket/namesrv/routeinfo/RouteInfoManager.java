package college.rocket.namesrv.routeinfo;

import college.rocket.common.DataVersion;
import college.rocket.common.MixAll;
import college.rocket.common.TopicConfig;
import college.rocket.common.namesrv.RegisterBrokerResult;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;
import college.rocket.common.protocol.route.BrokerData;
import college.rocket.common.protocol.route.QueueData;
import college.rocket.common.protocol.route.TopicRouteData;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 15:32
 * Version:V1.0
 */
@Slf4j
public class RouteInfoManager {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final HashMap<String/* clusterName */, Set<String/* brokerName */>> clusterAddrTable;
    private final HashMap<String/* brokerName */, BrokerData> brokerAddrTable;
    private final HashMap<String/* brokerAddr */, BrokerLiveInfo> brokerLiveTable;
    private final HashMap<String/* brokerAddr */, List<String>/* Filter Server */> filterServerTable;

    public RouteInfoManager() {
        this.clusterAddrTable = new HashMap(32);
        this.brokerAddrTable = new HashMap(128);
        this.brokerLiveTable = new HashMap<String, BrokerLiveInfo>(256);
        this.filterServerTable = new HashMap(256);
    }

    public TopicRouteData pickupTopicRouteData(final String topic) {
        return null;
    }

    public RegisterBrokerResult registerBroker(final String clusterName,
                                               final String brokerAddr,
                                               final String brokerName,
                                               final long brokerId,
                                               final String haServerAddr,
                                               final TopicConfigSerializeWrapper topicConfigWrapper,
                                               final List<String> filterServerList,
                                               final Channel channel) {
        RegisterBrokerResult result = new RegisterBrokerResult();
        try {
            try {
                //读写锁进行写锁  可中断的
                this.lock.writeLock().lockInterruptibly();

                //集群名
                Set<String> brokerNames = this.clusterAddrTable.get(clusterName);
                if (null == brokerNames) {
                    brokerNames = new HashSet();
                    this.clusterAddrTable.put(clusterName, brokerNames);
                }
                brokerNames.add(brokerName);

                boolean registerFirst = false;
                //从broker地址列表找数据
                BrokerData brokerData = this.brokerAddrTable.get(brokerName);
                if (null == brokerData) {
                    registerFirst = true;
                    brokerData = new BrokerData(clusterName, brokerName, new HashMap());
                    this.brokerAddrTable.put(brokerName, brokerData);
                }

                if (null != topicConfigWrapper
                        && MixAll.MASTER_ID == brokerId) {
                    if (this.isBrokerTopicConfigChanged(brokerAddr, topicConfigWrapper.getDataVersion())
                            || registerFirst) {
                        //拿到broker的所有Topic信息
                        ConcurrentMap<String, TopicConfig> tcTable =
                                topicConfigWrapper.getTopicConfigTable();
                        if (tcTable != null) {
                            for (Map.Entry<String, TopicConfig> entry : tcTable.entrySet()) {
                                //创建更新队列信息。把主题，broker信息赋值到队列  这个是最核心的代码
                                this.createAndUpdateQueueData(brokerName, entry.getValue());
                            }
                        }
                    }
                }

            } finally {
                this.lock.writeLock().unlock();
            }
        } catch (Exception e) {
            log.error("registerBroker Exception", e);
        }
        return result;
    }

    private void createAndUpdateQueueData(String brokerName, TopicConfig topicConfig) {
        QueueData queueData = new QueueData();
        queueData.setBrokerName(brokerName);
        queueData.setWriteQueueNums(topicConfig.getWriteQueueNums());
        queueData.setReadQueueNums(topicConfig.getReadQueueNums());
        queueData.setPerm(topicConfig.getPerm());
//        queueData.setTopicSynFlag(topicConfig.getTopicSysFlag());
//
//        List<QueueData> queueDataList = this.topicQueueTable.get(topicConfig.getTopicName());
//        if (null == queueDataList) {
//            queueDataList = new LinkedList<QueueData>();
//            queueDataList.add(queueData);
//            this.topicQueueTable.put(topicConfig.getTopicName(), queueDataList);
//            log.info("new topic registered, {} {}", topicConfig.getTopicName(), queueData);
//        } else {
//            boolean addNewOne = true;
//
//            Iterator<QueueData> it = queueDataList.iterator();
//            while (it.hasNext()) {
//                QueueData qd = it.next();
//                if (qd.getBrokerName().equals(brokerName)) {
//                    if (qd.equals(queueData)) {
//                        addNewOne = false;
//                    } else {
//                        log.info("topic changed, {} OLD: {} NEW: {}", topicConfig.getTopicName(), qd,
//                                queueData);
//                        it.remove();
//                    }
//                }
//            }
//
//            if (addNewOne) {
//                queueDataList.add(queueData);
//            }
//        }
    }

    private boolean isBrokerTopicConfigChanged(String brokerAddr, DataVersion dataVersion) {
        DataVersion prev = queryBrokerTopicConfig(brokerAddr);
        return null == prev || !prev.equals(dataVersion);
    }

    public DataVersion queryBrokerTopicConfig(final String brokerAddr) {
        BrokerLiveInfo prev = this.brokerLiveTable.get(brokerAddr);
        if (prev != null) {
            return prev.getDataVersion();
        }
        return null;
    }

    @Data
    class BrokerLiveInfo {
        private long lastUpdateTimestamp;
        private DataVersion dataVersion;
        private Channel channel;
        private String haServerAddr;

        public BrokerLiveInfo(long lastUpdateTimestamp, DataVersion dataVersion, Channel channel,
                              String haServerAddr) {
            this.lastUpdateTimestamp = lastUpdateTimestamp;
            this.dataVersion = dataVersion;
            this.channel = channel;
            this.haServerAddr = haServerAddr;
        }
    }

    /**
     * 存活Broker表，Broker地址表，队列的broker，集群的broker，清楚掉
     * @param remoteAddr
     * @param channel
     */
    public void onChannelDestroy(String remoteAddr, Channel channel) {
        String brokerAddrFound = null;
        //用存活表查询，加读锁
        if (channel != null) {
            try {
                try {
                    this.lock.readLock().lockInterruptibly();
                    Iterator<Map.Entry<String, BrokerLiveInfo>> itBrokerLiveTable =
                            this.brokerLiveTable.entrySet().iterator();
                    while (itBrokerLiveTable.hasNext()) {
                        Map.Entry<String, BrokerLiveInfo> entry = itBrokerLiveTable.next();
                        if (entry.getValue().getChannel() == channel) {
                            brokerAddrFound = entry.getKey();
                            break;
                        }
                    }
                } finally {
                    this.lock.readLock().unlock();
                }
            } catch (Exception e) {
                log.error("onChannelDestroy Exception", e);
            }
        }

        if (null == brokerAddrFound) {
            brokerAddrFound = remoteAddr;
        } else {
            log.info(" the broker's channel destroyed, {}, clean it's data structure at once", brokerAddrFound);
        }

        if (!StringUtils.hasLength(brokerAddrFound)) {
            try {
                try {
                    //加写锁
                    lock.writeLock().lockInterruptibly();
                    brokerLiveTable.remove(brokerAddrFound);
                    filterServerTable.remove(brokerAddrFound);
                    String brokerNameFound = null;
                    boolean removeBrokerName = false;
                    Iterator<Map.Entry<String, BrokerData>> itBrokerAddrTable =
                            brokerAddrTable.entrySet().iterator();
                    while (itBrokerAddrTable.hasNext() && null == brokerNameFound) {
                        BrokerData brokerData = itBrokerAddrTable.next().getValue();
                        Iterator<Map.Entry<Long, String>> it = brokerData.getBrokerAddrs().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Long, String> entry = it.next();
                            Long brokerId = entry.getKey();
                            String brokerAddr = entry.getValue();
                            if (brokerAddr.equals(brokerAddrFound)) {
                                brokerNameFound = brokerData.getBrokerName();
                                it.remove();
                                log.info("remove brokerAddr[{}, {}] from brokerAddrTable, because channel destroyed",
                                        brokerId, brokerAddr);
                                break;
                            }

                        }
                    }
                } finally {
                    this.lock.writeLock().unlock();
                }
            } catch (Exception e) {
                log.error("onChannelDestroy Exception", e);
            }
        }

    }


}
