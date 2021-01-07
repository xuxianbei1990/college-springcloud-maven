package college.rocket.remoting.exception;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:13
 * Version:V1.0
 */
public class RemotingTimeoutException extends RemotingException {
    public RemotingTimeoutException(String message) {
        super(message);
    }

    public RemotingTimeoutException(String addr, long timeoutMillis, Throwable cause) {
        super("wait response on the channel <" + addr + "> timeout, " + timeoutMillis + "(ms)", cause);
    }
}
