package college.rocket.common.protocol.body;

import college.rocket.common.DataVersion;
import college.rocket.common.TopicConfig;
import college.rocket.remoting.protocol.RemotingSerializable;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:51
 * Version:V1.0
 */
@Data
public class TopicConfigSerializeWrapper extends RemotingSerializable {

    private DataVersion dataVersion = new DataVersion();

    private ConcurrentMap<String, TopicConfig> topicConfigTable =
            new ConcurrentHashMap<String, TopicConfig>();
}
