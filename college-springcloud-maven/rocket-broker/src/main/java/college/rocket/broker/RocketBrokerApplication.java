package college.rocket.broker;

import college.rocket.common.BrokerConfig;
import college.rocket.remoting.netty.NettyClientConfig;
import college.rocket.remoting.netty.NettyServerConfig;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocket.store.config.MessageStoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class RocketBrokerApplication {

    public static void main(String[] args) {
        start(createBrokerController(args));
    }

    private static void start(BrokerController controller) {
        controller.start();
        String tip = "The broker[" + controller.getBrokerConfig().getBrokerName() + ", "
                + controller.getBrokerAddr() + "] boot success. serializeType=" + RemotingCommand.getSerializeTypeConfigInThisServer();

        if (null != controller.getBrokerConfig().getNamesrvAddr()) {
            tip += " and name server is " + controller.getBrokerConfig().getNamesrvAddr();
        }

        System.out.printf("%s%n", tip);
    }

    public static BrokerController createBrokerController(String[] args) {

        //获取配置文件
        final BrokerConfig brokerConfig = new BrokerConfig();
        final NettyServerConfig nettyServerConfig = new NettyServerConfig();
        final NettyClientConfig nettyClientConfig = new NettyClientConfig();
        final MessageStoreConfig messageStoreConfig = new MessageStoreConfig();

        final BrokerController controller = new BrokerController(
                brokerConfig,
                nettyServerConfig,
                nettyClientConfig,
                messageStoreConfig);

        boolean initResult = false;
        try {
            initResult = controller.initialize();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        if (!initResult) {
            throw new RuntimeException("初始化失败");
        }
        return controller;
    }

}
