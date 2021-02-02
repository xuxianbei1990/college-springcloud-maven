package college.rocketmq.client.impl.consumer;

import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocketmq.client.consumer.PullResult;
import college.rocketmq.client.impl.DefaultMQPushConsumerImpl;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 10:46
 * Version:V1.0
 */
public class PullCallbackImpl implements PullCallback {

    private final DefaultMQPushConsumerImpl defaultMQPushConsumer;

    private final PullRequest pullRequest;

    private final SubscriptionData subscriptionData;

    public PullCallbackImpl(DefaultMQPushConsumerImpl defaultMQPushConsumer,
                            PullRequest pullRequest, SubscriptionData subscriptionData) {
        this.defaultMQPushConsumer = defaultMQPushConsumer;
        this.pullRequest = pullRequest;
        this.subscriptionData = subscriptionData;
    }

    @Override
    public void onSuccess(PullResult pullResult) {
        if (pullResult == null) {
            return;
        }
        pullResult = defaultMQPushConsumer.getPullAPIWrapper().processPullResult(pullRequest.getMessageQueue(), pullResult,
                subscriptionData);
        switch (pullResult.getPullStatus()) {
            case FOUND:
                pullResultFound();
                break;
            case NO_NEW_MSG:
                pullRequest.setNextOffset(pullResult.getNextBeginOffset());
                defaultMQPushConsumer.correctTagsOffset(pullRequest);
                defaultMQPushConsumer.executePullRequestImmediately(pullRequest);
        }

    }

    private void pullResultFound() {

    }

    @Override
    public void onException(Throwable e) {

    }
}
