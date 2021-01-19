package college.rocket.common.protocol.header;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 11:43
 * Version:V1.0
 */
@Data
public class SendMessageRequestHeader {
    private String producerGroup;
    private String topic;
    private String defaultTopic;

    private Integer defaultTopicQueueNums;

    private Integer queueId;

    private String properties;

    private Integer reconsumeTimes;
}
