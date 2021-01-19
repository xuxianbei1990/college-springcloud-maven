package college.rocketmq.client.producer;

import college.rocket.common.message.Message;
import college.rocket.common.protocol.NamespaceUtil;
import college.rocket.common.topic.TopicValidator;
import college.rocket.remoting.RPCHook;
import college.rocket.remoting.exception.RemotingException;
import college.rocketmq.client.ClientConfig;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;
import college.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/18
 * Time: 15:14
 * Version:V1.0
 */
@Data
public class DefaultMQProducer extends ClientConfig implements MQProducer {


    protected final transient DefaultMQProducerImpl defaultMQProducerImpl;

    private int retryTimesWhenSendAsyncFailed = 2;

    private String producerGroup;
    private int sendMsgTimeout = 3000;

    private String createTopicKey = TopicValidator.AUTO_CREATE_TOPIC_KEY_TOPIC;

    private int retryTimesWhenSendFailed = 2;

    private volatile int defaultTopicQueueNums = 4;

    public DefaultMQProducer(final String producerGroup) {
        this(null, producerGroup, null);
    }

    public DefaultMQProducer(final String namespace, final String producerGroup, RPCHook rpcHook) {
        this.namespace = namespace;
        this.producerGroup = producerGroup;
        defaultMQProducerImpl = new DefaultMQProducerImpl(this, rpcHook);
    }

    @Override
    public void start() throws MQClientException {
        this.setProducerGroup(withNamespace(this.producerGroup));
        this.defaultMQProducerImpl.start();
    }

    @Override
    public SendResult send(Message msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException {
        msg.setTopic(withNamespace(msg.getTopic()));
        return this.defaultMQProducerImpl.send(msg);
    }

    public String withNamespace(String resource) {
        return NamespaceUtil.wrapNamespace(this.getNamespace(), resource);
    }
}
