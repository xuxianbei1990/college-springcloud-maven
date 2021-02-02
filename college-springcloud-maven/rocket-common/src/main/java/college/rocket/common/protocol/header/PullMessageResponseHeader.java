package college.rocket.common.protocol.header;

import college.rocket.remoting.CommandCustomHeader;
import college.rocket.remoting.exception.RemotingCommandException;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 14:04
 * Version:V1.0
 */
public class PullMessageResponseHeader implements CommandCustomHeader {
    @Override
    public void checkFields() throws RemotingCommandException {

    }
}
