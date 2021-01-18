package college.rocket.common.protocol.header.namesrv;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.exception.RemotingCommandException;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:27
 * Version:V1.0
 */
@Data
public class GetRouteInfoRequestHeader implements CommandCustomHeader {
    private String topic;

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
