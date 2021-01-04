package college.rocketmq.client.consumer.listener;

import college.rocket.common.message.MessageExt;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:16
 * Version:V1.0
 */
public interface MessageListenerConcurrently extends MessageListener {

    ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs,
                                             final ConsumeConcurrentlyContext context);
}
