package college.rocket.common.protocol.namesrv;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.exception.RemotingCommandException;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 16:36
 * Version:V1.0
 */
@Data
public class RegisterBrokerResponseHeader implements CommandCustomHeader {

    private String masterAddr;

    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
