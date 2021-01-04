package college.rocketmq.client.impl.consumer;

import college.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import college.rocketmq.client.impl.ConsumeMessageService;
import college.rocketmq.client.impl.DefaultMQPushConsumerImpl;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:15
 * Version:V1.0
 */
public class ConsumeMessageConcurrentlyService implements ConsumeMessageService {

    private final DefaultMQPushConsumerImpl defaultMQPushConsumerImpl;
    private final MessageListenerConcurrently messageListener;

    public ConsumeMessageConcurrentlyService(DefaultMQPushConsumerImpl defaultMQPushConsumerImpl,
                                             MessageListenerConcurrently messageListener) {
        this.defaultMQPushConsumerImpl = defaultMQPushConsumerImpl;
        this.messageListener = messageListener;
    }

    @Override
    public void start() {
        //这里就干了一件事情，定时清理过期消息？
    }
}
