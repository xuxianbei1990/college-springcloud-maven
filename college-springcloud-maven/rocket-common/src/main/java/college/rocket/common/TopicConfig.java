package college.rocket.common;

import college.rocket.common.constant.PermName;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/15
 * Time: 17:27
 * Version:V1.0
 */
@Data
public class TopicConfig {
    public static int defaultReadQueueNums = 16;
    public static int defaultWriteQueueNums = 16;
    private int readQueueNums = defaultReadQueueNums;
    private int writeQueueNums = defaultWriteQueueNums;

    private int perm = PermName.PERM_READ | PermName.PERM_WRITE;
}
