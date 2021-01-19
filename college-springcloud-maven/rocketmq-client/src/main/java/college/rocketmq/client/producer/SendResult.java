package college.rocketmq.client.producer;

import college.rocket.common.message.MessageQueue;

/**
 * @author: xuxianbei
 * Date: 2021/1/18
 * Time: 15:16
 * Version:V1.0
 */
public class SendResult {

    private SendStatus sendStatus;
    private String msgId;
    private MessageQueue messageQueue;
    private long queueOffset;
    private String transactionId;
    private String offsetMsgId;
    private String regionId;
    private boolean traceOn = true;

    public SendResult(SendStatus sendStatus, String msgId, String offsetMsgId, MessageQueue messageQueue,
                      long queueOffset) {
        this.sendStatus = sendStatus;
        this.msgId = msgId;
        this.offsetMsgId = offsetMsgId;
        this.messageQueue = messageQueue;
        this.queueOffset = queueOffset;
    }
}
