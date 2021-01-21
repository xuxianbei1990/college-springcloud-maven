package college.rocket.common.protocol.header;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.exception.RemotingCommandException;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 11:43
 * Version:V1.0
 */
@Data
public class SendMessageRequestHeader implements CommandCustomHeader {
    private String producerGroup;
    private String topic;
    private String defaultTopic;

    private Integer defaultTopicQueueNums;

    private Integer queueId;

    private String properties;

    private Integer reconsumeTimes;

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
