package college.rocket.broker.topic;

import college.rocket.broker.BrokerController;
import college.rocket.common.ConfigManager;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:53
 * Version:V1.0
 */
public class TopicConfigManager extends ConfigManager {

    private transient BrokerController brokerController;

    public TopicConfigManager(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public TopicConfigSerializeWrapper buildTopicConfigSerializeWrapper() {

        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        return topicConfigSerializeWrapper;
    }
}
