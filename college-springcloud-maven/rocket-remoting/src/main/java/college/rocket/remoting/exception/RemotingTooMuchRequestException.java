package college.rocket.remoting.exception;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 16:22
 * Version:V1.0
 */
public class RemotingTooMuchRequestException extends RemotingException  {

    public RemotingTooMuchRequestException(String message) {
        super(message);
    }
}
