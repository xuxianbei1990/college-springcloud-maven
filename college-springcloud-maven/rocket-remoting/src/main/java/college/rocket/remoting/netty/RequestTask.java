package college.rocket.remoting.netty;

import college.rocket.remoting.protocol.RemotingCommand;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/12
 * Time: 17:25
 * Version:V1.0
 */
@Data
public class RequestTask implements Runnable {

    private final Runnable runnable;
    private final Channel channel;
    private final RemotingCommand request;
    private boolean stopRun = false;

    public RequestTask(Runnable runnable, Channel channel, RemotingCommand request) {
        this.runnable = runnable;
        this.channel = channel;
        this.request = request;
    }

    @Override
    public void run() {
        if (!this.stopRun)
            this.runnable.run();
    }
}
