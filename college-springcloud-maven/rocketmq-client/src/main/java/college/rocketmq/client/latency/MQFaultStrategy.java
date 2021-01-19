package college.rocketmq.client.latency;

import college.rocket.common.message.MessageQueue;
import college.rocketmq.client.impl.producer.TopicPublishInfo;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 10:44
 * Version:V1.0
 */
public class MQFaultStrategy {
    public MessageQueue selectOneMessageQueue(TopicPublishInfo tpInfo, String lastBrokerName) {
        return tpInfo.selectOneMessageQueue(lastBrokerName);
    }
}
