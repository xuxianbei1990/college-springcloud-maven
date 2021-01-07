package college.rocket.remoting.netty;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 16:40
 * Version:V1.0
 */
@Data
public class NettyEvent {

    private final NettyEventType type;
    private final String remoteAddr;
    private final Channel channel;

    public NettyEvent(NettyEventType type, String remoteAddr, Channel channel) {
        this.type = type;
        this.remoteAddr = remoteAddr;
        this.channel = channel;
    }
}
