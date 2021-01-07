package college.rocket.common.protocol.route;

import college.rocket.remoting.protocol.RemotingSerializable;
import lombok.Data;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:08
 * Version:V1.0
 */
@Data
public class TopicRouteData extends RemotingSerializable {

    private List<QueueData> queueDatas;
    private List<BrokerData> brokerDatas;

    public TopicRouteData cloneTopicRouteData() {
        TopicRouteData topicRouteData = new TopicRouteData();
        return topicRouteData;
    }
}
