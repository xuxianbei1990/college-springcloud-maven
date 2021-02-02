package college.rocketmq.client.impl.consumer;

import college.rocket.common.message.MessageQueue;
import college.rocketmq.client.impl.ProcessQueue;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:23
 * Version:V1.0
 */
@Data
public class PullRequest {
    private ProcessQueue processQueue;
    private String consumerGroup;
    private long nextOffset;

    private MessageQueue messageQueue;



}
