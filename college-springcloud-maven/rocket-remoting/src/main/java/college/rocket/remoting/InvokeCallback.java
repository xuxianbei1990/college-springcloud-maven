package college.rocket.remoting;

import college.rocket.remoting.netty.ResponseFuture;

/**
 * @author: xuxianbei
 * Date: 2021/1/7
 * Time: 13:45
 * Version:V1.0
 */
public interface InvokeCallback {

    void operationComplete(final ResponseFuture responseFuture);
}
