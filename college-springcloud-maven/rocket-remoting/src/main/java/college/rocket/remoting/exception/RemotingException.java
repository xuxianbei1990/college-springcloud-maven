package college.rocket.remoting.exception;

/**
 * @author: xuxianbei
 * Date: 2021/1/6
 * Time: 15:12
 * Version:V1.0
 */
public class RemotingException extends Exception {
    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }
}
