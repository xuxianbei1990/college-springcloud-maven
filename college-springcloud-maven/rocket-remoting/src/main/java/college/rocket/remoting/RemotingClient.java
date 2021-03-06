package college.rocket.remoting;

import college.rocket.remoting.exception.RemotingConnectException;
import college.rocket.remoting.exception.RemotingSendRequestException;
import college.rocket.remoting.exception.RemotingTimeoutException;
import college.rocket.remoting.exception.RemotingTooMuchRequestException;
import college.rocket.remoting.protocol.RemotingCommand;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:53
 * Version:V1.0
 */
public interface RemotingClient extends RemotingService {

    List<String> getNameServerAddressList();

    void updateNameServerAddressList(final List<String> addrs);

    RemotingCommand invokeSync(final String addr, final RemotingCommand request,
                               final long timeoutMillis) throws InterruptedException, RemotingConnectException,
            RemotingSendRequestException, RemotingTimeoutException;

    void invokeAsync(final String addr, final RemotingCommand request, final long timeoutMillis,
                     final InvokeCallback invokeCallback) throws InterruptedException, RemotingConnectException,
            RemotingTooMuchRequestException, RemotingTimeoutException, RemotingSendRequestException;
}
