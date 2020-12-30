package college.rocketmq.client.consumer;

import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.impl.DefaultMQPushConsumerImpl;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 15:46
 * Version:V1.0
 */
@Data
public class DefaultMQPushConsumer extends ClientConfig implements MQPushConsumer {

    private String consumerGroup;

    protected final transient DefaultMQPushConsumerImpl defaultMQPushConsumerImpl;

    @Override
    public void start() throws MQClientException {
        setConsumerGroup("college");

    }
}
