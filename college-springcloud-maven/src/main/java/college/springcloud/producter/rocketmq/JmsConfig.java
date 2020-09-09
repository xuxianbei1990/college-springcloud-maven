package college.springcloud.producter.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

/**
 * @author: xuxianbei
 * Date: 2020/9/7
 * Time: 11:36
 * Version:V1.0
 */
public class JmsConfig {
    /**
     * 多个逗号分隔 "127.12.15.6:9876;127.12.15.6:9877";
     */
    public static final String NAME_SERVER = "192.168.138.3:9876";

    /**
     * 主题
     */
    public static final String TOPIC_SIMPLE = "topic_simple";

    /**
     * 生产组名称
     */
    public static final String PRODUCER_GROUP = "simple_unique_group_name";


    @Test
    public void SyncProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer(PRODUCER_GROUP);
        producer.setNamesrvAddr(NAME_SERVER);
        producer.start();
        producer.createTopic("TagA", TOPIC_SIMPLE, 1);
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
}

