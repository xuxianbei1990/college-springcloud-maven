package college.rocketmq.client.consumer;

import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import college.rocketmq.client.consumer.listener.MessageListener;
import college.rocketmq.client.consumer.listener.MessageListenerConcurrently;
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
    private MessageListener messageListener;

    private int pullBatchSize = 32;

    protected final transient DefaultMQPushConsumerImpl defaultMQPushConsumerImpl;


    public DefaultMQPushConsumer() {
        defaultMQPushConsumerImpl = new DefaultMQPushConsumerImpl(this, null);
        messageListener = (MessageListenerConcurrently) (msgs, context) -> {
            System.out.println(" Receive New Messages: " + msgs);
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        };
        defaultMQPushConsumerImpl.setMessageListenerInner(messageListener);
    }

    @Override
    public void start() throws MQClientException {
        setConsumerGroup("college");
        defaultMQPushConsumerImpl.start();

    }

    public void subscribe(String topic, String subExpression) throws MQClientException {
        this.defaultMQPushConsumerImpl.subscribe(withNamespace(topic), subExpression);
    }
}
