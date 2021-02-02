package college.rocketmq.client.impl;

import college.rocketmq.client.impl.consumer.RebalanceImpl;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 15:34
 * Version:V1.0
 */
public class RebalancePushImpl extends RebalanceImpl {

    public RebalancePushImpl(DefaultMQPushConsumerImpl defaultMQPushConsumerImpl) {
        super(null, null, null, null);

    }
}
