package college.rocketmq.client.impl.producer;

import college.rocket.remoting.RPCHook;
import college.rocketmq.client.ClientConfig;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:31
 * Version:V1.0
 */
@Data
public class DefaultMQProducer extends ClientConfig implements MQProducer {

    private String producerGroup;

    protected final transient DefaultMQProducerImpl defaultMQProducerImpl;

    public DefaultMQProducer(final String producerGroup) {
        this(null, producerGroup, null);
    }

    public DefaultMQProducer(final String namespace, final String producerGroup, RPCHook rpcHook) {
        this.namespace = namespace;
        this.producerGroup = producerGroup;
        defaultMQProducerImpl = new DefaultMQProducerImpl(this, rpcHook);
    }

    public void resetClientConfig(final ClientConfig cc) {

    }
}
