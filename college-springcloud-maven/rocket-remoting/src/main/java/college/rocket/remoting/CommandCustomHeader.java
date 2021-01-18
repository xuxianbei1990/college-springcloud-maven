package college.rocket.remoting;

import college.rocket.remoting.exception.RemotingCommandException;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:32
 * Version:V1.0
 */
public interface CommandCustomHeader {
    void checkFields() throws RemotingCommandException;
}
