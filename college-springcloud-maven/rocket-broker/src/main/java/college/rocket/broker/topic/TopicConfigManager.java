package college.rocket.broker.topic;

import college.rocket.broker.BrokerController;
import college.rocket.common.ConfigManager;
import college.rocket.common.DataVersion;
import college.rocket.common.TopicConfig;
import college.rocket.common.protocol.body.TopicConfigSerializeWrapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:53
 * Version:V1.0
 */
public class TopicConfigManager extends ConfigManager {

    private transient BrokerController brokerController;

    private final DataVersion dataVersion = new DataVersion();

    private final ConcurrentMap<String, TopicConfig> topicConfigTable = new ConcurrentHashMap(1024);

    public TopicConfigManager(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public TopicConfigSerializeWrapper buildTopicConfigSerializeWrapper() {

        TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
        return topicConfigSerializeWrapper;
    }

    public TopicConfig selectTopicConfig(final String topic) {
        return this.topicConfigTable.get(topic);
    }

    public DataVersion getDataVersion() {
        return dataVersion;
    }
}
