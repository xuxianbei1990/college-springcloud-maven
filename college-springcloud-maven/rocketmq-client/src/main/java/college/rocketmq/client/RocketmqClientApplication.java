package college.rocketmq.client;

import college.rocket.common.message.Message;
import college.rocket.remoting.exception.RemotingException;
import college.rocketmq.client.consumer.DefaultMQPushConsumer;
import college.rocketmq.client.consumer.exception.MQClientException;
import college.rocketmq.client.exception.MQBrokerException;
import college.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class RocketmqClientApplication {

    public static void main(String[] args) throws MQClientException, RemotingException, InterruptedException, MQBrokerException {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.start();
        DefaultMQProducer defaultMqProducer = new DefaultMQProducer("XXB");
        defaultMqProducer.start();
        Message msg = new Message();
        msg.setTopic("Simple");
        msg.setBody("XXB".getBytes());
        defaultMqProducer.send(msg);
    }

}
