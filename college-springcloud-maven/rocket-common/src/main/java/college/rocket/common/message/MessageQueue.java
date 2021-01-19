package college.rocket.common.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 10:37
 * Version:V1.0
 */
@Data
public class MessageQueue implements Comparable<MessageQueue>, Serializable {

    private String brokerName;
    private String topic;
    private int queueId;

    public MessageQueue(String topic, String brokerName, Integer queueId) {
        this.topic = topic;
        this.brokerName = brokerName;
        this.queueId = queueId;
    }

    @Override
    public int compareTo(MessageQueue o) {
        return 0;
    }
}
