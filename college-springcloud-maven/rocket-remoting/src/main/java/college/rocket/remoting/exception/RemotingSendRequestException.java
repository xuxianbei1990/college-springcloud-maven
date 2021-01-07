package college.rocket.remoting.exception;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:13
 * Version:V1.0
 */
public class RemotingSendRequestException extends RemotingException {

    public RemotingSendRequestException(String addr, Throwable cause) {
        super("send request to <" + addr + "> failed", cause);
    }
}
