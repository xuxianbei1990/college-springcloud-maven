package college.rocket.broker.client;

import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 11:03
 * Version:V1.0
 */
@Data
public class ClientChannelInfo {

    private final String clientId;

    private final Channel channel;

    /**
     * JAVA((byte) 0),
     */
    private final byte language;

    private final int version;

    private volatile long lastUpdateTimestamp = System.currentTimeMillis();


    public ClientChannelInfo(Channel channel, String clientId, byte language, int version) {
        this.channel = channel;
        this.clientId = clientId;
        this.language = language;
        this.version = version;
    }
}
