package college.rocketmq.client.impl;

import college.rocket.common.ServiceState;
import college.rocket.remoting.RPCHook;
import college.rocketmq.client.consumer.DefaultMQPushConsumer;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.consumer.listener.MessageListener;
import college.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import college.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService;
import college.rocketmq.client.impl.consumer.PullAPIWrapper;
import college.rocketmq.client.impl.factory.MQClientInstance;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 16:01
 * Version:V1.0
 */
@Data
public class DefaultMQPushConsumerImpl implements MQConsumerInner {

    private volatile ServiceState serviceState = ServiceState.CREATE_JUST;
    private final DefaultMQPushConsumer defaultMQPushConsumer;

    private ConsumeMessageService consumeMessageService;

    private MessageListener messageListenerInner;

    private final RPCHook rpcHook;
    private PullAPIWrapper pullAPIWrapper;

    private MQClientInstance mQClientFactory;

    public DefaultMQPushConsumerImpl(DefaultMQPushConsumer defaultMQPushConsumer, RPCHook rpcHook) {
        this.defaultMQPushConsumer = defaultMQPushConsumer;
        this.rpcHook = rpcHook;
    }

    public synchronized void start() throws MQClientException {
        switch (serviceState) {
            case CREATE_JUST:
                this.serviceState = ServiceState.START_FAILED;
                this.mQClientFactory = MQClientManager.getInstance().getOrCreateMQClientInstance(this.defaultMQPushConsumer, this.rpcHook);

                this.pullAPIWrapper = new PullAPIWrapper(
                        mQClientFactory, this.defaultMQPushConsumer.getConsumerGroup(), false);
                consumeMessageService = new ConsumeMessageConcurrentlyService(this, (MessageListenerConcurrently) this.getMessageListenerInner());
                consumeMessageService.start();
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
}
