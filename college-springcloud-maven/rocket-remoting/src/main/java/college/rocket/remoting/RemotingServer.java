package college.rocket.remoting;

import college.rocket.remoting.netty.NettyRequestProcessor;

import java.util.concurrent.ExecutorService;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 11:13
 * Version:V1.0
 */
public interface RemotingServer extends RemotingService {

    void registerProcessor(final int requestCode, final NettyRequestProcessor processor,
                           final ExecutorService executor);

    void registerDefaultProcessor(final NettyRequestProcessor processor, final ExecutorService executor);
}
