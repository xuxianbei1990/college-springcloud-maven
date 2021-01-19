package college.rocket.common.protocol.header;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.annotation.CFNotNull;
import college.rocket.remoting.exception.RemotingCommandException;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 16:32
 * Version:V1.0
 */
public class SendMessageRequestHeaderV2  implements CommandCustomHeader {

    @CFNotNull
    private String a; // producerGroup;
    @CFNotNull
    private String b; // topic;
    @CFNotNull
    private String c; // defaultTopic;
    @CFNotNull
    private Integer d; // defaultTopicQueueNums;
    @CFNotNull
    private Integer e; // queueId;
    @CFNotNull
    private Integer f; // sysFlag;
    @CFNotNull
    private Long g; // bornTimestamp;
    @CFNotNull
    private Integer h; // flag;

    private String i; // properties;

    private Integer j; // reconsumeTimes;

    private boolean k; // unitMode = false;

    private Integer l; // consumeRetryTimes

    private boolean m; //batch

    @Override
    public void checkFields() throws RemotingCommandException {

    }

    public static SendMessageRequestHeaderV2 createSendMessageRequestHeaderV2(final SendMessageRequestHeader v1) {
        SendMessageRequestHeaderV2 v2 = new SendMessageRequestHeaderV2();
        v2.a = v1.getProducerGroup();
        v2.b = v1.getTopic();
        v2.c = v1.getDefaultTopic();
        v2.d = v1.getDefaultTopicQueueNums();
        v2.e = v1.getQueueId();
//        v2.f = v1.getSysFlag();
//        v2.g = v1.getBornTimestamp();
//        v2.h = v1.getFlag();
        v2.i = v1.getProperties();
        v2.j = v1.getReconsumeTimes();
//        v2.k = v1.isUnitMode();
//        v2.l = v1.getMaxReconsumeTimes();
//        v2.m = v1.isBatch();
        return v2;
    }
}
