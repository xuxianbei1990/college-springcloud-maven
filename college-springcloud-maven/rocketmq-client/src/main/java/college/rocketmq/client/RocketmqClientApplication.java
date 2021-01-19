package college.rocketmq.client;

import college.rocketmq.client.consumer.DefaultMQPushConsumer;
import college.rocketmq.client.consumer.exception.MQClientException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class RocketmqClientApplication {

    public static void main(String[] args) throws MQClientException {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.start();


    }

}
