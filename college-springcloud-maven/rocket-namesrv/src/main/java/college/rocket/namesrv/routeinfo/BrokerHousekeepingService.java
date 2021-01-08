package college.rocket.namesrv.routeinfo;

import college.rocket.namesrv.NamesrvController;
import college.rocket.remoting.ChannelEventListener;
import io.netty.channel.Channel;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 11:20
 * Version:V1.0
 */
public class BrokerHousekeepingService implements ChannelEventListener {

    private final NamesrvController namesrvController;

    public BrokerHousekeepingService(NamesrvController namesrvController) {
        this.namesrvController = namesrvController;
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
