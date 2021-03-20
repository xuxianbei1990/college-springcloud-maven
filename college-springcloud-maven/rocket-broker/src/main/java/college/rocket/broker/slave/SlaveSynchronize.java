package college.rocket.broker.slave;

import college.rocket.broker.BrokerController;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 15:06
 * Version:V1.0
 */
@Slf4j
@Data
public class SlaveSynchronize {

    private volatile String masterAddr = null;

    private final BrokerController brokerController;

    public SlaveSynchronize(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public void syncAll() {
        //从主地址获取TopicConfig更新从，并且序列化到本地磁盘
        this.syncTopicConfig();
        //一样把消费的游标同步过来
        this.syncConsumerOffset();
//        this.syncDelayOffset();
        //同步订阅组游标
//        this.syncSubscriptionGroupConfig();
    }

    private void syncConsumerOffset() {
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null && !masterAddrBak.equals(brokerController.getBrokerAddr())) {

        }
    }

    private void syncTopicConfig() {
        //确定从master
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null && !masterAddrBak.equals(brokerController.getBrokerAddr())) {
            try {
                //好像是同步全局配置信息
                //从哪里拿到从节点的服务器地址
                //从主地址发起调用拿到TopicConfig信息，然后同步给自己（Slave）
                TopicConfigSerializeWrapper topicWrapper =
                        this.brokerController.getBrokerOuterAPI().getAllTopicConfig(masterAddrBak);
                if (!this.brokerController.getTopicConfigManager().getDataVersion()
                        .equals(topicWrapper.getDataVersion())) {

                    this.brokerController.getTopicConfigManager().getDataVersion()
                            .assignNewOne(topicWrapper.getDataVersion());

                    this.brokerController.getTopicConfigManager().getDataVersion()
                            .assignNewOne(topicWrapper.getDataVersion());
                    this.brokerController.getTopicConfigManager().getTopicConfigTable().clear();
                    this.brokerController.getTopicConfigManager().getTopicConfigTable()
                            .putAll(topicWrapper.getTopicConfigTable());
                    this.brokerController.getTopicConfigManager().persist();
                }

            } catch (Exception e) {
                log.error("SyncTopicConfig Exception, {}", masterAddrBak, e);
            }
        }
    }
}
