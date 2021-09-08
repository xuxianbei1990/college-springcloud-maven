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
    /**
     * 监听端口 默认会被初始化为9876
     */
    private int listenPort = 8888;

    /**
     * send oneway 消息请求井发度（ Broker 端参数）
     */
    private int serverOnewaySemaphoreValue = 256;

    /**
     * 异步消息发送最大并发度（ Broker 端参数）
     */
    private int serverAsyncSemaphoreValue = 64;

    /**
     * Netty public 务线程池线程个数， Netty 络设计，
     * 根据业务类型会创建不同的线程池，比如处理消息发送、消息消费、心跳检测等
     * 如果该业务类型（Request Code）未注册线程池， public 线程池执行
     */
    private int serverCallbackExecutorThreads = 0;

    /**
     * : IO 线程池线程个数，主要是 NameServer、Broker 端解析请求、
     * 返回相应的线程个数，这类线程主要是处理网络请求的，解析请求包， 然后转发到
     * 各个业务线程池完成具体的业务操作，然后将结果再返回调用方
     */
    private int serverSelectorThreads = 3;
    /**
     * 业务线程池个数
     */
    private int serverWorkerThreads = 8;

    /**
     * 网络连接最大空 闲时间，默认 120s 如果连接
     * 空闲时间超过该参数设置的值，连接将被关闭
     */
    private int serverChannelMaxIdleTimeSeconds = 120;

    /**
     * 网络 socket 发送缓存区大小， 默认 64k
     */
    private int serverSocketSndBufSize = NettySystemConfig.socketSndbufSize;
    /**
     * 网络 socket 发送缓存区大小， 默认 64k
     */
    private int serverSocketRcvBufSize = NettySystemConfig.socketRcvbufSize;
}
