package college.springcloud.producter.consumer;

import college.springcloud.producter.model.StudentVo;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

/**
 * @author: xuxianbei
 * Date: 2020/9/15
 * Time: 14:26
 * Version:V1.0
 */
@Service
@RocketMQMessageListener(topic = "${rocketmq.topic}", consumerGroup = "${rocketmq.consumer.group}",
        selectorExpression = "97"
//     测试顺序消费用的   , consumeMode = ConsumeMode.ORDERLY
)
//以接口方式实现主要是为了回调
public class StudentConsumer implements RocketMQListener<StudentVo> {
    @Override
    public void onMessage(StudentVo message) {
        System.out.println(message);
    }
}
