package college.rocket.broker.client;

import college.rocket.remoting.common.RemotingHelper;
import college.rocket.remoting.common.RemotingUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 11:12
 * Version:V1.0
 */
@Slf4j
public class ConsumerManager {

    private static final long CHANNEL_EXPIRED_TIMEOUT = 1000 * 120;

    private final ConsumerIdsChangeListener consumerIdsChangeListener;

    private final ConcurrentMap<String/* Group */, ConsumerGroupInfo> consumerTable =
            new ConcurrentHashMap<String, ConsumerGroupInfo>(1024);

    public ConsumerManager(final ConsumerIdsChangeListener consumerIdsChangeListener) {
        this.consumerIdsChangeListener = consumerIdsChangeListener;
    }


    /**
     * 从消费组中获取消息的客户端，
     * 如果长期没有更新时间，那么主动关闭通道
     */
    public void scanNotActiveChannel() {
        Iterator<Map.Entry<String, ConsumerGroupInfo>> it = this.consumerTable.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ConsumerGroupInfo> next = it.next();
            String group = next.getKey();
            ConsumerGroupInfo consumerGroupInfo = next.getValue();
            ConcurrentMap<Channel, ClientChannelInfo> channelInfoTable =
                    consumerGroupInfo.getChannelInfoTable();

            Iterator<Map.Entry<Channel, ClientChannelInfo>> itChannel = channelInfoTable.entrySet().iterator();
            while (itChannel.hasNext()) {
                Map.Entry<Channel, ClientChannelInfo> nextChannel = itChannel.next();
                ClientChannelInfo clientChannelInfo = nextChannel.getValue();
                long diff = System.currentTimeMillis() - clientChannelInfo.getLastUpdateTimestamp();
                if (diff > CHANNEL_EXPIRED_TIMEOUT) {
                    log.warn(
                            "SCAN: remove expired channel from ConsumerManager consumerTable. channel={}, consumerGroup={}",
                            RemotingHelper.parseChannelRemoteAddr(clientChannelInfo.getChannel()), group);
                    RemotingUtil.closeChannel(clientChannelInfo.getChannel());
                    itChannel.remove();
                }
            }

            if (channelInfoTable.isEmpty()) {
                log.warn(
                        "SCAN: remove expired channel from ConsumerManager consumerTable, all clear, consumerGroup={}",
                        group);
                it.remove();
            }
        }
    }

}
