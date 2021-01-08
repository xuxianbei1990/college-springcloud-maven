package college.rocket.remoting.netty;

import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:44
 * Version:V1.0
 */
@Data
public class NettyClientConfig {

    private int clientWorkerThreads = 4;

    private int connectTimeoutMillis = 3000;

    private int clientSocketSndBufSize = NettySystemConfig.socketSndbufSize;
    private int clientSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
    private int clientOnewaySemaphoreValue = NettySystemConfig.CLIENT_ONEWAY_SEMAPHORE_VALUE;
    private int clientAsyncSemaphoreValue = NettySystemConfig.CLIENT_ASYNC_SEMAPHORE_VALUE;

    private int clientChannelMaxIdleTimeSeconds = 120;

    private boolean clientCloseSocketIfTimeout = false;


}
