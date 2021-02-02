package college.rocketmq.client.impl;

import college.rocket.common.ServiceState;
import college.rocket.common.filter.FilterAPI;
import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocket.remoting.RPCHook;
import college.rocketmq.client.consumer.DefaultMQPushConsumer;
import college.rocketmq.client.consumer.PullResult;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.consumer.listener.MessageListener;
import college.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import college.rocketmq.client.consumer.store.OffsetStore;
import college.rocketmq.client.impl.consumer.*;
import college.rocketmq.client.impl.factory.MQClientInstance;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 16:01
 * Version:V1.0
 */
@Slf4j
@Data
public class DefaultMQPushConsumerImpl implements MQConsumerInner {

    private volatile ServiceState serviceState = ServiceState.CREATE_JUST;
    private static final long PULL_TIME_DELAY_MILLS_WHEN_FLOW_CONTROL = 50;
    private static final long BROKER_SUSPEND_MAX_TIME_MILLIS = 1000 * 15;
    private static final long CONSUMER_TIMEOUT_MILLIS_WHEN_SUSPEND = 1000 * 30;
    private long pullTimeDelayMillsWhenException = 3000;
    private final RebalanceImpl rebalanceImpl = new RebalancePushImpl(this);
    private final DefaultMQPushConsumer defaultMQPushConsumer;

    private ConsumeMessageService consumeMessageService;

    private MessageListener messageListenerInner;

    private final RPCHook rpcHook;
    private PullAPIWrapper pullAPIWrapper;
    private OffsetStore offsetStore;

    private MQClientInstance mQClientFactory;

    private boolean consumeOrderly = false;

    public DefaultMQPushConsumerImpl(DefaultMQPushConsumer defaultMQPushConsumer, RPCHook rpcHook) {
        this.defaultMQPushConsumer = defaultMQPushConsumer;
        this.rpcHook = rpcHook;
    }

    public synchronized void start() throws MQClientException {
        switch (serviceState) {
            case CREATE_JUST:
                this.serviceState = ServiceState.START_FAILED;
                this.mQClientFactory = MQClientManager.getInstance().getOrCreateMQClientInstance(this.defaultMQPushConsumer, this.rpcHook);
                //拉取消息
                this.pullAPIWrapper = new PullAPIWrapper(
                        mQClientFactory, this.defaultMQPushConsumer.getConsumerGroup(), false);
                consumeMessageService = new ConsumeMessageConcurrentlyService(this, (MessageListenerConcurrently) this.getMessageListenerInner());
                consumeMessageService.start();
                //注册消息为了拉取消息
                boolean registerOK = mQClientFactory.registerConsumer(this.defaultMQPushConsumer.getConsumerGroup(), this);
                if (!registerOK) {
                    this.serviceState = ServiceState.CREATE_JUST;
                    throw new MQClientException("The consumer group[" + this.defaultMQPushConsumer.getConsumerGroup()
                            + "] has been created before, specify another name please.", null);
                }
                mQClientFactory.start();
                this.serviceState = ServiceState.RUNNING;
                break;
            case RUNNING:
            case START_FAILED:
            case SHUTDOWN_ALREADY:
//                throw new MQClientException("The PushConsumer service state not OK, maybe started once, "
//                        + this.serviceState
//                        + FAQUrl.suggestTodo(FAQUrl.CLIENT_SERVICE_NOT_OK),
//                        null);
            default:
                break;
        }
    }

    public void subscribe(String topic, String subExpression) throws MQClientException {
        try {
            SubscriptionData subscriptionData = FilterAPI.buildSubscriptionData(this.defaultMQPushConsumer.getConsumerGroup(),
                    topic, subExpression);
            this.rebalanceImpl.getSubscriptionInner().put(topic, subscriptionData);
            if (this.mQClientFactory != null) {
                this.mQClientFactory.sendHeartbeatToAllBrokerWithLock();
            }
        } catch (Exception e) {
            throw new MQClientException("subscription exception", e);
        }
    }

    public void pullMessage(PullRequest pullRequest) {
        final ProcessQueue processQueue = pullRequest.getProcessQueue();
        if (processQueue.isDropped()) {
            log.info("the pull request[{}] is dropped.", pullRequest.toString());
            return;
        }
        pullRequest.getProcessQueue().setLastPullTimestamp(System.currentTimeMillis());

        //这里有两个方法一个是延后执行拉取请求。当请求到达阈值的时候， 把拉取消息的请求重新丢回队列中
        this.executePullRequestLater(pullRequest, PULL_TIME_DELAY_MILLS_WHEN_FLOW_CONTROL);

        final SubscriptionData subscriptionData = this.rebalanceImpl.getSubscriptionInner().get(pullRequest.getMessageQueue().getTopic());

        PullCallback pullCallback = new PullCallbackImpl(this, pullRequest, subscriptionData);

        String subExpression = null;
        int sysFlag = 0;
        long commitOffsetValue = 0L;
        try {
            this.pullAPIWrapper.pullKernelImpl(
                    pullRequest.getMessageQueue(),
                    subExpression,
                    subscriptionData.getExpressionType(),
                    subscriptionData.getSubVersion(),
                    pullRequest.getNextOffset(),
                    this.defaultMQPushConsumer.getPullBatchSize(),
                    sysFlag,
                    commitOffsetValue,
                    BROKER_SUSPEND_MAX_TIME_MILLIS,
                    CONSUMER_TIMEOUT_MILLIS_WHEN_SUSPEND,
                    CommunicationMode.ASYNC,
                    pullCallback
            );
    } catch (Exception e) {
        log.error("pullKernelImpl exception", e);
        this.executePullRequestLater(pullRequest, pullTimeDelayMillsWhenException);
    }
    }

    @Override
    public void doRebalance() {
        this.rebalanceImpl.doRebalance(this.isConsumeOrderly());
    }

    private void executePullRequestLater(final PullRequest pullRequest, final long timeDelay) {
        this.mQClientFactory.getPullMessageService().executePullRequestLater(pullRequest, timeDelay);
    }

    public void correctTagsOffset(PullRequest pullRequest) {
        if (0L == pullRequest.getProcessQueue().getMsgCount().get()) {
            this.offsetStore.updateOffset(pullRequest.getMessageQueue(), pullRequest.getNextOffset(), true);
        }
    }

    public void executePullRequestImmediately(final PullRequest pullRequest) {
        this.mQClientFactory.getPullMessageService().executePullRequestImmediately(pullRequest);
    }
}
