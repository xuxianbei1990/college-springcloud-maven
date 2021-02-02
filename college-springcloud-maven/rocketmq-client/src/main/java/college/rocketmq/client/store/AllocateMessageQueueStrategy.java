package college.rocketmq.client.store;

import college.rocket.common.message.MessageQueue;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 18:34
 * Version:V1.0
 */
public interface AllocateMessageQueueStrategy {

    List<MessageQueue> allocate(
            final String consumerGroup,
            final String currentCID,
            final List<MessageQueue> mqAll,
            final List<String> cidAll
    );


    String getName();
}
