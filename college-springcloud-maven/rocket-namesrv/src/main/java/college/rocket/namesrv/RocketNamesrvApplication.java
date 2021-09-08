package college.rocket.namesrv;

import college.rocket.common.ShutdownHookThread;
import college.rocket.common.namesrv.NamesrvConfig;
import college.rocket.remoting.netty.NettyServerConfig;
import college.rocket.remoting.protocol.RemotingCommand;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RocketNamesrvApplication {

    public static void main(String[] args) {
        try {
            NamesrvController controller = createNamesrvController(args);
            start(controller);
            String tip = "The Name Server boot success. serializeType=" + RemotingCommand.getSerializeTypeConfigInThisServer();
            log.info(tip);
            System.out.printf("%s%n", tip);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static NamesrvController start(final NamesrvController controller) throws Exception {
        if (null == controller) {
            throw new IllegalArgumentException("NamesrvController is null");
        }

        boolean initResult = controller.initialize();
        if (!initResult) {
//            controller.shutdown();
//            System.exit(-3);
            throw new RuntimeException("failure");
        }

        /**
         * 注册钩子函数
         * 常用的编程技巧，如果代码中使用了线程池，一种优雅停
         * 机的方式就是注册 JVM 钩子函数， JVM 进程关闭之前，先将线程池关闭 ，及时释放
         * 资源
         */
        Runtime.getRuntime().addShutdownHook(new ShutdownHookThread(() -> {
            controller.shutdown();
            return null;
        }));


        controller.start();

        return controller;
    }

    private static NamesrvController createNamesrvController(String[] args) {

        final NamesrvConfig namesrvConfig = new NamesrvConfig();
        final NettyServerConfig nettyServerConfig = new NettyServerConfig();
        nettyServerConfig.setListenPort(9876);


        final NamesrvController controller = new NamesrvController(namesrvConfig, nettyServerConfig);
        return controller;
    }

}
