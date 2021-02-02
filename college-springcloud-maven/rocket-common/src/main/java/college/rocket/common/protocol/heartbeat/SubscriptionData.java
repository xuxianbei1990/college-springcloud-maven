package college.rocket.common.protocol.heartbeat;

import college.rocket.common.filter.ExpressionType;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 15:30
 * Version:V1.0
 */
@Data
public class SubscriptionData {

    private String expressionType = ExpressionType.TAG;

    private long subVersion = System.currentTimeMillis();
}
