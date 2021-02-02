package college.rocketmq.client.impl.consumer;

import college.rocket.common.message.MessageQueue;
import college.rocket.common.protocol.heartbeat.MessageModel;
import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocketmq.client.impl.ProcessQueue;
import college.rocketmq.client.impl.factory.MQClientInstance;
import college.rocketmq.client.store.AllocateMessageQueueStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 15:33
 * Version:V1.0
 */
@Slf4j
@Data
public abstract class RebalanceImpl {

    final ConcurrentMap<String /* topic */, SubscriptionData> subscriptionInner =
            new ConcurrentHashMap<String, SubscriptionData>();

    protected final ConcurrentMap<String/* topic */, Set<MessageQueue>> topicSubscribeInfoTable =
            new ConcurrentHashMap<String, Set<MessageQueue>>();

    protected final ConcurrentMap<MessageQueue, ProcessQueue> processQueueTable = new ConcurrentHashMap<MessageQueue, ProcessQueue>(64);

    protected MQClientInstance mQClientFactory;

    protected MessageModel messageModel;
    protected AllocateMessageQueueStrategy allocateMessageQueueStrategy;
    protected String consumerGroup;

    public RebalanceImpl(String consumerGroup, MessageModel messageModel,
                         AllocateMessageQueueStrategy allocateMessageQueueStrategy,
                         MQClientInstance mQClientFactory) {
        this.consumerGroup = consumerGroup;
        this.messageModel = messageModel;
        this.allocateMessageQueueStrategy = allocateMessageQueueStrategy;
        this.mQClientFactory = mQClientFactory;
    }

    public void doRebalance(boolean isOrder) {
        Map<String, SubscriptionData> subTable = this.getSubscriptionInner();
        if (subTable != null) {
            for (final Map.Entry<String, SubscriptionData> entry : subTable.entrySet()) {
                final String topic = entry.getKey();
                try {
                    this.rebalanceByTopic(topic, isOrder);
                } catch (Throwable e) {
//                    if (!topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
//                        log.warn("rebalanceByTopic Exception", e);
//                    }
                }
            }
        }
    }

    private void rebalanceByTopic(String topic, boolean isOrder) {
        switch (messageModel) {
            case CLUSTERING: {
                Set<MessageQueue> mqSet = this.topicSubscribeInfoTable.get(topic);
                List<String> cidAll = this.mQClientFactory.findConsumerIdList(topic, consumerGroup);

                if (mqSet != null && cidAll != null) {
                    List<MessageQueue> mqAll = new ArrayList<MessageQueue>();
                    mqAll.addAll(mqSet);

                    Collections.sort(mqAll);
                    Collections.sort(cidAll);

                    AllocateMessageQueueStrategy strategy = this.allocateMessageQueueStrategy;

                    List<MessageQueue> allocateResult = null;

                    try {
                        allocateResult = strategy.allocate(
                                this.consumerGroup,
                                this.mQClientFactory.getClientId(),
                                mqAll,
                                cidAll);
                    } catch (Throwable e) {
                        log.error("AllocateMessageQueueStrategy.allocate Exception. allocateMessageQueueStrategyName={}", strategy.getName(),
                                e);
                        return;
                    }

                    Set<MessageQueue> allocateResultSet = new HashSet<MessageQueue>();
                    if (allocateResult != null) {
                        allocateResultSet.addAll(allocateResult);
                    }

                    boolean changed = this.updateProcessQueueTableInRebalance(topic, allocateResultSet, isOrder);
                }
            }
        }
    }

    private boolean updateProcessQueueTableInRebalance(final String topic, final Set<MessageQueue> mqSet,
                                                       final boolean isOrder) {
        boolean changed = false;

        Iterator<Map.Entry<MessageQueue, ProcessQueue>> it = this.processQueueTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<MessageQueue, ProcessQueue> next = it.next();
            MessageQueue mq = next.getKey();
            ProcessQueue pq = next.getValue();

            if (mq.getTopic().equals(topic)) {

            }

        }
        List<PullRequest> pullRequestList = new ArrayList<PullRequest>();

        for (MessageQueue mq : mqSet) {

        }

        this.dispatchPullRequest(pullRequestList);
        return false;
    }

    public abstract void dispatchPullRequest(List<PullRequest> pullRequestList);

}
