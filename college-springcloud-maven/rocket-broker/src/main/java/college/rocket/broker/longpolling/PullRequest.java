package college.rocket.broker.longpolling;

import college.rocket.common.protocol.heartbeat.SubscriptionData;
import college.rocket.remoting.protocol.RemotingCommand;
import college.rocket.store.MessageFilter;
import io.netty.channel.Channel;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/2/1
 * Time: 14:31
 * Version:V1.0
 */
@Data
public class PullRequest {
    private final RemotingCommand requestCommand;
    private final Channel clientChannel;
    private final long timeoutMillis;
    private final long suspendTimestamp;
    private final long pullFromThisOffset;
    private final SubscriptionData subscriptionData;
    private final MessageFilter messageFilter;

    public PullRequest(RemotingCommand requestCommand, Channel clientChannel, long timeoutMillis, long suspendTimestamp,
                       long pullFromThisOffset, SubscriptionData subscriptionData,
                       MessageFilter messageFilter) {
        this.requestCommand = requestCommand;
        this.clientChannel = clientChannel;
        this.timeoutMillis = timeoutMillis;
        this.suspendTimestamp = suspendTimestamp;
        this.pullFromThisOffset = pullFromThisOffset;
        this.subscriptionData = subscriptionData;
        this.messageFilter = messageFilter;
    }
}
