package college.springcloud.producter.consumer;

import college.springcloud.producter.model.StudentVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @author: xuxianbei
 * Date: 2020/12/16
 * Time: 14:51
 * Version:V1.0
 */
//@Service
@RocketMQMessageListener(topic = "${rocketmq.topic}", consumerGroup = "${rocketmq.consumer.group}",
        selectorExpression = "98")
public class StudentConsumerTrace implements RocketMQListener<StudentVo> {

    @Override
    public void onMessage(StudentVo message) {
        System.out.println(message);
    }
}