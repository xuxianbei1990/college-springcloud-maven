package college.rocketmq.client.consumer;

import college.rocketmq.client.impl.consumer.PullStatus;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 10:33
 * Version:V1.0
 */
@Data
public class PullResult {
    private final long nextBeginOffset;
    private final PullStatus pullStatus;
}
