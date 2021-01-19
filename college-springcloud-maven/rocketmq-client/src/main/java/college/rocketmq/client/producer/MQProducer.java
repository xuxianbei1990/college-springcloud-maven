package college.rocketmq.client.producer;

import college.rocket.common.message.Message;
import college.rocket.remoting.exception.RemotingException;
import college.rocketmq.client.MQAdmin;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;

/**
 * @author: xuxianbei
 * Date: 2021/1/18
 * Time: 15:15
 * Version:V1.0
 */
public interface MQProducer extends MQAdmin {

    void start() throws MQClientException;

    SendResult send(final Message msg) throws MQClientException, RemotingException, MQBrokerException,
            InterruptedException;
}
