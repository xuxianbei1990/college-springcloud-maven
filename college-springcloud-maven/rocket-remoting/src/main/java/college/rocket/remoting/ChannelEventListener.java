package college.rocket.remoting;

import io.netty.channel.Channel;

/**
 * @author: xuxianbei
 * Date: 2020/12/31
 * Time: 16:56
 * Version:V1.0
 */
public interface ChannelEventListener {

    void onChannelConnect(final String remoteAddr, final Channel channel);

    void onChannelClose(final String remoteAddr, final Channel channel);

    void onChannelException(final String remoteAddr, final Channel channel);

    void onChannelIdle(final String remoteAddr, final Channel channel);
}
