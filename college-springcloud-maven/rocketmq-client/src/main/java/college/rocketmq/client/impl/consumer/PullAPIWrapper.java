package college.rocketmq.client.impl.consumer;

import college.rocket.common.MixAll;
import college.rocket.common.message.MessageQueue;
import college.rocket.common.protocol.header.PullMessageRequestHeader;
import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocket.remoting.exception.RemotingException;
import college.rocketmq.client.consumer.PullResult;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;
import college.rocketmq.client.impl.CommunicationMode;
import college.rocketmq.client.impl.FindBrokerResult;
import college.rocketmq.client.impl.factory.MQClientInstance;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:10
 * Version:V1.0
 */
public class PullAPIWrapper {

    private final MQClientInstance mQClientFactory;
    private final String consumerGroup;
    private final boolean unitMode;

    public PullAPIWrapper(MQClientInstance mQClientFactory, String consumerGroup, boolean unitMode) {
        this.mQClientFactory = mQClientFactory;
        this.consumerGroup = consumerGroup;
        this.unitMode = unitMode;
    }

    public PullResult processPullResult(final MessageQueue mq, final PullResult pullResult,
                                        final SubscriptionData subscriptionData) {
        return null;
    }

    public PullResult pullKernelImpl(
            final MessageQueue mq,
            final String subExpression,
            final String expressionType,
            final long subVersion,
            final long offset,
            final int maxNums,
            final int sysFlag,
            final long commitOffset,
            final long brokerSuspendMaxTimeMillis,
            final long timeoutMillis,
            final CommunicationMode communicationMode,
            final PullCallback pullCallback
    ) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        FindBrokerResult findBrokerResult =
                this.mQClientFactory.findBrokerAddressInSubscribe(mq.getBrokerName(),
                        this.recalculatePullFromWhichNode(mq), false);

        if (null == findBrokerResult) {
            this.mQClientFactory.updateTopicRouteInfoFromNameServer(mq.getTopic());
            findBrokerResult =
                    this.mQClientFactory.findBrokerAddressInSubscribe(mq.getBrokerName(),
                            this.recalculatePullFromWhichNode(mq), false);
        }

        if (findBrokerResult != null) {
            int sysFlagInner = sysFlag;
            PullMessageRequestHeader requestHeader = new PullMessageRequestHeader();
            requestHeader.setConsumerGroup(this.consumerGroup);
            requestHeader.setTopic(mq.getTopic());
            requestHeader.setQueueId(mq.getQueueId());
            requestHeader.setQueueOffset(offset);
            requestHeader.setMaxMsgNums(maxNums);
            requestHeader.setSysFlag(sysFlagInner);
            requestHeader.setCommitOffset(commitOffset);
            requestHeader.setSuspendTimeoutMillis(brokerSuspendMaxTimeMillis);
            requestHeader.setSubscription(subExpression);
            requestHeader.setSubVersion(subVersion);
            requestHeader.setExpressionType(expressionType);
            String brokerAddr = findBrokerResult.getBrokerAddr();
            PullResult pullResult = this.mQClientFactory.getMQClientAPIImpl().pullMessage(
                    brokerAddr,
                    requestHeader,
                    timeoutMillis,
                    communicationMode,
                    pullCallback);
            return pullResult;
        }
        return null;
    }

    public long recalculatePullFromWhichNode(final MessageQueue mq) {
        return MixAll.MASTER_ID;
    }

}
