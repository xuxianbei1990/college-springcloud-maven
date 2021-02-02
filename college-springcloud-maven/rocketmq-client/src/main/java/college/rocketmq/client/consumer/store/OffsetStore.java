package college.rocketmq.client.consumer.store;

import college.rocket.common.message.MessageQueue;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 10:58
 * Version:V1.0
 */
public interface OffsetStore {

    /**
     * Update the offset,store it in memory
     */
    void updateOffset(final MessageQueue mq, final long offset, final boolean increaseOnly);
}
