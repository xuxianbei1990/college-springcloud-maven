package college.rocket.namesrv;

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
