package college.rocket.broker.client;

import college.rocket.broker.BrokerController;
import college.rocket.remoting.ChannelEventListener;
import io.netty.channel.Channel;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:41
 * Version:V1.0
 */
public class ClientHousekeepingService implements ChannelEventListener {

    private final BrokerController brokerController;

    public ClientHousekeepingService(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {

    }

    @Override
    public void onChannelIdle(String remoteAddr, Channel channel) {

    }
}
