package college.rocket.remoting.exception;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 17:32
 * Version:V1.0
 */
public class RemotingCommandException extends RemotingException {
    public RemotingCommandException(String message) {
        super(message, null);
    }

    public RemotingCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
