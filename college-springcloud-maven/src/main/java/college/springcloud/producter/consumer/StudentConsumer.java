package college.springcloud.producter.consumer;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Service;

/**
 * @author: xuxianbei
 * Date: 2020/9/15
 * Time: 14:26
 * Version:V1.0
 */
@Service
@RocketMQMessageListener(topic = "${rocket.topic}", consumerGroup = "")
public class StudentConsumer {
}
