package college.rocket.common.protocol.route;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/7
 * Time: 16:46
 * Version:V1.0
 */
@Data
public class QueueData {
    private String brokerName;
    private int readQueueNums;
    private int writeQueueNums;
    private int perm;
    private int topicSynFlag;
}
