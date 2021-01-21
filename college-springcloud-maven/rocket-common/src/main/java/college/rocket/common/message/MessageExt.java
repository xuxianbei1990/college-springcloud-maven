package college.rocket.common.message;

import lombok.Data;

import java.net.SocketAddress;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:23
 * Version:V1.0
 */
@Data
public class MessageExt extends Message {

    private int queueId;

    private SocketAddress bornHost;

    private SocketAddress storeHost;

    private int reconsumeTimes;
}
