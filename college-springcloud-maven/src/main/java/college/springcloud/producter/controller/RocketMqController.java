package college.springcloud.producter.controller;

import college.springcloud.producter.model.CfChargeCommon;
import college.springcloud.producter.model.StudentVo;
import com.alibaba.fastjson.JSONObject;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
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
     *
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
            studentVo.setOrderType(StudentVo.OrderTypeEnum.CREATE_ORDER);
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            //默认实现方式SelectMessageQueueByHash  第三个参数一般是唯一标识，例如订单号
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
            System.out.println("噼啪噼啪一顿操作");
            studentVo.setOrderType(StudentVo.OrderTypeEnum.PAY_ORDER);
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
            System.out.println("QWER 点燃");
            studentVo.setOrderType(StudentVo.OrderTypeEnum.FINISHED_ORDER);
            studentVo.setName(kingRole);
            studentVo.setAge(18);
            rocketMQTemplate.syncSendOrderly(topic, studentVo, studentVo.getName());
        }

        return "result";
    }

    /**
     * 测试延迟消费
     *
     * @return
     */
    @GetMapping("producer/delay")
    public SendResult producerDelay() {
        SendResult sendResult;
        StudentVo studentVo = new StudentVo();
        studentVo.setOrderType(StudentVo.OrderTypeEnum.DELAY_ORDER);
        studentVo.setAge(23);
        studentVo.setName("鬼泣1代");
        Message<?> message = MessageBuilder.withPayload(studentVo).build();
        rocketMQTemplate.syncSend(topic, message, 500, 1);
        studentVo.setOrderType(StudentVo.OrderTypeEnum.DELAY_ORDER);
        studentVo.setAge(24);
        studentVo.setName("鬼泣2代");
        message = MessageBuilder.withPayload(studentVo).build();
        rocketMQTemplate.syncSend(topic, message, 500, 2);

        studentVo.setOrderType(StudentVo.OrderTypeEnum.DELAY_ORDER);
        studentVo.setAge(25);
        studentVo.setName("鬼泣3代");
        message = MessageBuilder.withPayload(studentVo).build();
        sendResult = rocketMQTemplate.syncSend(topic, message, 500, 3);
        return sendResult;
    }

    /**
     * 消息回溯  默认支持时间维度消息回溯
     *
     * @return
     */
    @GetMapping("producer/msg/trace")
    public SendResult producerMsgTrace() {
        StudentVo studentVo = new StudentVo();
        studentVo.setOrderType(StudentVo.OrderTypeEnum.TRACE_ORDER);
        studentVo.setAge(26);
        studentVo.setName("鬼泣回溯");
        return rocketMQTemplate.syncSend(topic, studentVo);
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

    /**
     * 结论，rocketMQ 本身不支持同一个项目里面订阅多个主题，也不支持订阅多个tag
     * 要实现同一个订阅多个主题的话，只能在消息本身定义消息类别。
     * 优先级队列：定义一个新主题或者tag，把消息发过去，新启动一个实例来消费，
     *
     * @return
     */
    @GetMapping("producer/msg/tag")
    public SendResult producerMsgTag() {
        StudentVo studentVo = new StudentVo();
        studentVo.setOrderType(StudentVo.OrderTypeEnum.TRACE_ORDER);
        studentVo.setAge(26);
        studentVo.setName("鬼泣Tag97");
        rocketMQTemplate.syncSend(topic + ":97", studentVo);

        studentVo.setOrderType(StudentVo.OrderTypeEnum.TRACE_ORDER);
        studentVo.setAge(26);
        studentVo.setName("鬼泣Tag98");
        rocketMQTemplate.syncSend(topic + ":98", studentVo);

        studentVo.setOrderType(StudentVo.OrderTypeEnum.TRACE_ORDER);
        studentVo.setAge(26);
        studentVo.setName("鬼泣Tag99");
        return rocketMQTemplate.syncSend(topic + ":99", studentVo);
    }

    @GetMapping("test/mcn")
    public SendResult testMcn() {
        CfChargeCommon cfChargeCommon = new CfChargeCommon();
        cfChargeCommon.setChargeType(1);
        cfChargeCommon.setArapType("AR");
        cfChargeCommon.setChargeSourceCode("xxb" + (new Random()).nextInt());
        cfChargeCommon.setChargeSourceDetail("xxb" + (new Random()).nextInt());
        cfChargeCommon.setAmountPp(BigDecimal.ZERO);
        cfChargeCommon.setBalance("xxb");
        cfChargeCommon.setFinanceEntity("ssd");
        cfChargeCommon.setInvoiceTitle("asdf");
        cfChargeCommon.setInvoiceTitleName("qweqwe");
        cfChargeCommon.setCreateBy(1L);
        cfChargeCommon.setCreateName("xxf");
        cfChargeCommon.setTenantId(2L);
        cfChargeCommon.setCompanyId(3L);
        cfChargeCommon.setUpdateDate(LocalDateTime.now());
        cfChargeCommon.setUpdateBy(2L);
        cfChargeCommon.setSettTemplate(1);
        cfChargeCommon.setTaxRate(BigDecimal.ONE);
        return rocketMQTemplate.syncSend("MQ_mcn_charge_finance_test", JSONObject.toJSONString(cfChargeCommon));
    }

}
