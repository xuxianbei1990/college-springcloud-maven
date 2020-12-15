package college.springcloud.producter.controller;

import college.springcloud.producter.model.StudentVo;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xuxianbei
 * Date: 2020/9/15
 * Time: 11:52
 * Version:V1.0
 */
@RequestMapping("rocket")
@RestController
public class RocketMqController {

    @Value("${rocketmq.topic}")
    private String topic;


    @Resource
    @Lazy
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("producer")
    public SendResult producer() {
        StudentVo studentVo = new StudentVo();
        studentVo.setName("lu卡尔");
        studentVo.setAge(18);
        SendResult sendResult = rocketMQTemplate.syncSend(topic, studentVo);
        return sendResult;
    }

    /**
     * 测试顺序消费，需要RocketMQMessageListener的consumeMode = ConsumeMode.ORDERLY
     * @return
     */
    @GetMapping("producer/order")
    public String producerOrder() {
        /**
         * 业务：每个拳皇角色完成都要发送创建订单，支付成功，完成。
         */
        String[] kingRoles = new String[]{"库拉2001", "八神", "草", "大蛇"};
        for (String kingRole : kingRoles) {
            StudentVo studentVo = new StudentVo();
            studentVo.setOrderType(StudentVo.OrderTypeEnum.CREATE_ORDER.getKey());
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            //默认实现方式SelectMessageQueueByHash  第三个参数一般是唯一标识，例如订单号
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
            System.out.println("噼啪噼啪一顿操作");
            studentVo.setOrderType(StudentVo.OrderTypeEnum.PAY_ORDER.getKey());
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
            System.out.println("QWER 点燃");
            studentVo.setOrderType(StudentVo.OrderTypeEnum.FINISHED_ORDER.getKey());
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
        }

        return "result";
    }


    /**
     * rockmq事务到底怎么实现的
     *
     * @return
     */
    @GetMapping("transaction")
    public String transaction() {
        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            Message msg = MessageBuilder.withPayload("rocketMQTemplate transactional message" + i)
                    .setHeader(RocketMQHeaders.TRANSACTION_ID, "KEY_" + i).build();
            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction("college-transation-topic"
                    + ":" + tags[i % tags.length], msg, null);
            System.out.printf("------rocketMQTemplate send Transactional msg body = %s , sendResult=%s %n",
                    msg.getPayload(), sendResult.getSendStatus());
        }
        return "success";
    }


    @RocketMQTransactionListener
    class TransactionListenerImpl implements RocketMQLocalTransactionListener {

        private AtomicInteger transactionIndex = new AtomicInteger(0);

        private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            System.out.printf("#### executeLocalTransaction is executed, msgTransactionId=%s %n",
                    transId);
            int value = transactionIndex.getAndIncrement();
            int status = value % 3;
            localTrans.put(transId, status);
            if (status == 0) {
                // Return local transaction with success(commit), in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # COMMIT # Simulating msg %s related local transaction exec succeeded! ### %n", msg.getPayload());
                return RocketMQLocalTransactionState.COMMIT;
            }

            if (status == 1) {
                // Return local transaction with failure(rollback) , in this case,
                // this message will not be checked in checkLocalTransaction()
                System.out.printf("    # ROLLBACK # Simulating %s related local transaction exec failed! %n", msg.getPayload());
                return RocketMQLocalTransactionState.ROLLBACK;
            }

            System.out.printf("    # UNKNOW # Simulating %s related local transaction exec UNKNOWN! \n");
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            String transId = (String) msg.getHeaders().get(RocketMQHeaders.TRANSACTION_ID);
            RocketMQLocalTransactionState retState = RocketMQLocalTransactionState.COMMIT;
            Integer status = localTrans.get(transId);
            if (null != status) {
                switch (status) {
                    case 0:
                        retState = RocketMQLocalTransactionState.UNKNOWN;
                        break;
                    case 1:
                        retState = RocketMQLocalTransactionState.COMMIT;
                        break;
                    case 2:
                        retState = RocketMQLocalTransactionState.ROLLBACK;
                        break;
                }
            }
            System.out.printf("------ !!! checkLocalTransaction is executed once," +
                            " msgTransactionId=%s, TransactionState=%s status=%s %n",
                    transId, retState, status);
            return retState;
        }
    }
}
