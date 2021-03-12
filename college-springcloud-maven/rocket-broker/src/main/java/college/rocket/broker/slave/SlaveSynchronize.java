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
        this.syncTopicConfig();
//        this.syncConsumerOffset();
//        this.syncDelayOffset();
//        this.syncSubscriptionGroupConfig();
    }

    private void syncTopicConfig() {
        String masterAddrBak = this.masterAddr;
        if (masterAddrBak != null && !masterAddrBak.equals(brokerController.getBrokerAddr())) {
            try {
//                TopicConfigSerializeWrapper topicWrapper =
//                        this.brokerController.getBrokerOuterAPI().getAllTopicConfig(masterAddrBak);


            } catch (Exception e) {
                log.error("SyncTopicConfig Exception, {}", masterAddrBak, e);
            }
        }
    }
}
