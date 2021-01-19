package college.rocketmq.client.impl.producer;

import college.rocket.common.message.MessageQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 10:34
 * Version:V1.0
 */
public class TopicPublishInfo {

    private List<MessageQueue> messageQueueList = new ArrayList<MessageQueue>();

    public boolean ok() {
        return null != this.messageQueueList && !this.messageQueueList.isEmpty();
    }

    public MessageQueue selectOneMessageQueue(String lastBrokerName) {
        if (lastBrokerName == null) {
            return selectOneMessageQueue();
        }
        return null;
    }

    public MessageQueue selectOneMessageQueue() {
        ThreadLocalRandom sendWhichQueue = ThreadLocalRandom.current();
        int index = sendWhichQueue.nextInt();
        int pos = Math.abs(index) % this.messageQueueList.size();
        if (pos < 0)
            pos = 0;
        return this.messageQueueList.get(pos);
    }
}
