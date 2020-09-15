package college.springcloud.producter.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2020/9/7
 * Time: 11:36
 * Version:V1.0、
 * 消费组和生产组是不需要从控制台创建的
 * 主题是需要的
 */
public class RocketMqTest {
    /**
     * 多个逗号分隔 "127.12.15.6:9876;127.12.15.6:9877";
     */
    public static final String NAME_SERVER = "192.168.138.3:9876";

    /**
     * 主题  需要自己手动创建主题
     */
    public static final String TOPIC_SIMPLE = "topic_simple";

    /**
     * 生产组名称
     */
    public static final String PRODUCER_GROUP = "simple_unique_group_name";




    /**
     * 第一个问题这个吞吐量多大？
     *
     * @throws Exception
     */
    @Test
    public void syncProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(NAME_SERVER);
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(TOPIC_SIMPLE /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            SendResult sendResult = producer.send(msg);
            System.out.printf("%s%n", sendResult);
        }
        //Shut down once the producer instance is not longer in use.
        producer.shutdown();
    }

    /**
     * 异步发送消息
     *
     * @throws Exception
     */
    @Test
    public void asyncProducer() throws Exception {
//Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        // Specify name server addresses.
        producer.setNamesrvAddr(NAME_SERVER);
        //Launch the instance.
        producer.start();
        producer.setRetryTimesWhenSendAsyncFailed(0);

        int messageCount = 100;
        final CountDownLatch countDownLatch = new CountDownLatch(messageCount);
        for (int i = 0; i < messageCount; i++) {
            try {
                final int index = i;
                Message msg = new Message(TOPIC_SIMPLE,
                        "TagA",
                        "OrderID188",
                        "Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
                producer.send(msg, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d OK %s %n", index, sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        countDownLatch.countDown();
                        System.out.printf("%-10d Exception %s %n", index, e);
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        countDownLatch.await(5, TimeUnit.SECONDS);
        producer.shutdown();
    }

    @Test
    public void oneWayProducer() throws Exception {
        //Instantiate with a producer group name.  这个组名有什么用处，区分？
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        // Specify name server addresses.
        producer.setNamesrvAddr(NAME_SERVER);
        //Launch the instance.
        producer.start();
        for (int i = 0; i < 100; i++) {
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message(TOPIC_SIMPLE /* Topic */,
                    "TagA" /* Tag */,
                    ("Hello RocketMQ " +
                            i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            producer.sendOneway(msg);
        }
        //Wait for sending to complete
        Thread.sleep(5000);
        producer.shutdown();
    }

    public static void testConsumer() throws Exception {
        // Instantiate with specified consumer group name.
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(PRODUCER_GROUP);

        // Specify name server addresses.
        consumer.setNamesrvAddr(NAME_SERVER);

        // Subscribe one more more topics to consume.
        consumer.subscribe(TOPIC_SIMPLE, "*");
        // Register callback to execute on arrival of messages fetched from brokers.
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
                                                            ConsumeConcurrentlyContext context) {
                System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        //Launch the consumer instance.
        consumer.start();

        System.out.printf("Consumer Started.%n");
    }

    public static void main(String[] args) {
        try {
            testConsumer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

