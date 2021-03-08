package college.rocket.broker.client;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 13:43
 * Version:V1.0
 */
@Data
public class ConsumerGroupInfo {

    private final ConcurrentMap<Channel, ClientChannelInfo> channelInfoTable =
            new ConcurrentHashMap<Channel, ClientChannelInfo>(16);
}
