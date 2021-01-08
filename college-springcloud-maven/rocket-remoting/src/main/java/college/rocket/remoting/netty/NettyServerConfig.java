package college.rocket.remoting.netty;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 11:01
 * Version:V1.0
 */
@Data
public class NettyServerConfig {
    private int listenPort = 8888;
    private int serverOnewaySemaphoreValue = 256;
    private int serverAsyncSemaphoreValue = 64;
    private int serverCallbackExecutorThreads = 0;
    private int serverSelectorThreads = 3;
    private int serverWorkerThreads = 8;
    private int serverChannelMaxIdleTimeSeconds = 120;

    private int serverSocketSndBufSize = NettySystemConfig.socketSndbufSize;
    private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
}
