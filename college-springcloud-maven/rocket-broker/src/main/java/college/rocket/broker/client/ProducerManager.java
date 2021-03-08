package college.rocket.broker.client;

import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.common.RemotingUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 生产者管理
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 10:44
 * Version:V1.0
 */
@Slf4j
public class ProducerManager {

    private static final long CHANNEL_EXPIRED_TIMEOUT = 1000 * 120;

    private final ConcurrentHashMap<String /* group name */, ConcurrentHashMap<Channel, ClientChannelInfo>> groupChannelTable =
            new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Channel> clientChannelTable = new ConcurrentHashMap<>();

    /**
     * 把长期不发消息的生产者通道关闭掉。
     */
    public void scanNotActiveChannel() {
        for (final Map.Entry<String, ConcurrentHashMap<Channel, ClientChannelInfo>> entry : this.groupChannelTable
                .entrySet()) {
            final String group = entry.getKey();
            final ConcurrentHashMap<Channel, ClientChannelInfo> chlMap = entry.getValue();

            Iterator<Map.Entry<Channel, ClientChannelInfo>> it = chlMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Channel, ClientChannelInfo> item = it.next();
                // final Integer id = item.getKey();
                final ClientChannelInfo info = item.getValue();

                long diff = System.currentTimeMillis() - info.getLastUpdateTimestamp();
                if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                    it.remove();
                    clientChannelTable.remove(info.getClientId());
                    log.warn(
                            "SCAN: remove expired channel[{}] from ProducerManager groupChannelTable, producer group name: {}",
                            RemotingHelper.parseChannelRemoteAddr(info.getChannel()), group);
                    RemotingUtil.closeChannel(info.getChannel());
                }
            }
        }
    }
}
