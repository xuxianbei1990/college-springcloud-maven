package college.rocket.common.protocol.header;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.annotation.CFNotNull;
import college.rocket.remoting.exception.RemotingCommandException;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 11:44
 * Version:V1.0
 */
@Data
public class PullMessageRequestHeader implements CommandCustomHeader {

    @CFNotNull
    private String consumerGroup;
    @CFNotNull
    private String topic;
    @CFNotNull
    private Integer queueId;
    @CFNotNull
    private Long queueOffset;
    @CFNotNull
    private Integer maxMsgNums;
    @CFNotNull
    private Integer sysFlag;
    @CFNotNull
    private Long commitOffset;
    @CFNotNull
    private Long suspendTimeoutMillis;

    private String subscription;
    @CFNotNull
    private Long subVersion;
    private String expressionType;

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
