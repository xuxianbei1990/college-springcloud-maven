package college.rocket.common.protocol.header;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.exception.RemotingCommandException;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 16:41
 * Version:V1.0
 */
@Data
public class SendMessageResponseHeader implements CommandCustomHeader {

    private Integer queueId;

    private String msgId;

    private Long queueOffset;

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
