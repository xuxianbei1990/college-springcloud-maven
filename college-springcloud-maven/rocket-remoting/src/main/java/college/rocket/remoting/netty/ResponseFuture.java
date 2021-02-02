package college.rocket.remoting.netty;

import college.rocket.remoting.InvokeCallback;
import college.rocket.remoting.common.SemaphoreReleaseOnlyOnce;
import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/1/7
 * Time: 13:43
 * Version:V1.0
 */
@Data
public class ResponseFuture {
    private final int opaque;
    private final Channel processChannel;
    private final long timeoutMillis;
    private final InvokeCallback invokeCallback;
    private final SemaphoreReleaseOnlyOnce once;
    private volatile boolean sendRequestOK = true;
    private volatile Throwable cause;
    private volatile RemotingCommand responseCommand;
    private final CountDownLatch countDownLatch = new CountDownLatch(1);
    private final long beginTimestamp = System.currentTimeMillis();

    public ResponseFuture(Channel channel, int opaque, long timeoutMillis, InvokeCallback invokeCallback,
                          SemaphoreReleaseOnlyOnce once) {
        this.opaque = opaque;
        this.processChannel = channel;
        this.timeoutMillis = timeoutMillis;
        this.invokeCallback = invokeCallback;
        this.once = once;
    }

    public void putResponse(final RemotingCommand responseCommand) {
        this.responseCommand = responseCommand;
        this.countDownLatch.countDown();
    }

    public RemotingCommand waitResponse(final long timeoutMillis) throws InterruptedException {
        this.countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS);
        return this.responseCommand;
    }

    public void release() {
        if (this.once != null) {
            this.once.release();
        }
    }

    public boolean isTimeout() {
        long diff = System.currentTimeMillis() - this.beginTimestamp;
        return diff > this.timeoutMillis;
    }
}
