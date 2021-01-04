package college.rocketmq.client.impl.consumer;

import college.rocketmq.client.impl.factory.MQClientInstance;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:10
 * Version:V1.0
 */
public class PullAPIWrapper {

    private final MQClientInstance mQClientFactory;
    private final String consumerGroup;
    private final boolean unitMode;

    public PullAPIWrapper(MQClientInstance mQClientFactory, String consumerGroup, boolean unitMode) {
        this.mQClientFactory = mQClientFactory;
        this.consumerGroup = consumerGroup;
        this.unitMode = unitMode;
    }
}
