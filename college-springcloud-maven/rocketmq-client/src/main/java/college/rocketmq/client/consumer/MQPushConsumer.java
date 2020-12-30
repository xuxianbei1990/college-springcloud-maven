package college.rocketmq.client.consumer;

import college.rocketmq.client.consumer.exception.MQClientException;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 15:50
 * Version:V1.0
 */
public interface MQPushConsumer extends MQConsumer {

    void start() throws MQClientException;
}
