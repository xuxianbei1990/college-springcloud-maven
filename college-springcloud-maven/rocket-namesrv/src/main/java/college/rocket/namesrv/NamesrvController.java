package college.rocket.namesrv;

import college.rocket.common.Configuration;
import college.rocket.common.ThreadFactoryImpl;
import college.rocket.common.namesrv.NamesrvConfig;
import college.rocket.namesrv.processor.DefaultRequestProcessor;
import college.rocket.namesrv.routeinfo.BrokerHousekeepingService;
import college.rocket.namesrv.routeinfo.RouteInfoManager;
import college.rocket.remoting.RemotingServer;
import college.rocket.remoting.netty.NettyRemotingServer;
import college.rocket.remoting.netty.NettyServerConfig;
import lombok.Data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 10:57
 * Version:V1.0
 */
@Data
public class NamesrvController {

    private RemotingServer remotingServer;
    private final NettyServerConfig nettyServerConfig;
    private final NamesrvConfig namesrvConfig;

    private BrokerHousekeepingService brokerHousekeepingService;
    private ExecutorService remotingExecutor;
    private final RouteInfoManager routeInfoManager;

    private Configuration configuration;

    public NamesrvController(NamesrvConfig namesrvConfig, NettyServerConfig nettyServerConfig) {
        this.namesrvConfig = namesrvConfig;
        this.nettyServerConfig = nettyServerConfig;
        this.routeInfoManager = new RouteInfoManager();
    }

    public boolean initialize() {
        //netty 服务端
        this.remotingServer = new NettyRemotingServer(this.nettyServerConfig, this.brokerHousekeepingService);
        this.remotingExecutor =
                Executors.newFixedThreadPool(nettyServerConfig.getServerWorkerThreads(), new ThreadFactoryImpl("RemotingExecutorThread_"));

        this.brokerHousekeepingService = new BrokerHousekeepingService(this);
        this.configuration = new Configuration(this.namesrvConfig, this.nettyServerConfig);

        this.registerProcessor();
        return true;
    }

    public void start() {
        this.remotingServer.start();
    }

    private void registerProcessor() {
//        if (namesrvConfig.isClusterTest()) {
//
//            this.remotingServer.registerDefaultProcessor(new ClusterTestRequestProcessor(this, namesrvConfig.getProductEnvName()),
//                    this.remotingExecutor);
//        } else {
            //默认注册进程
            this.remotingServer.registerDefaultProcessor(new DefaultRequestProcessor(this), this.remotingExecutor);
//        }
    }
}
