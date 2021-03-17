package college.rocket.common;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:32
 * Version:V1.0
 */
public class MixAll {

    public static final String CLIENT_INNER_PRODUCER_GROUP = "CLIENT_INNER_PRODUCER";
    public static final String RETRY_GROUP_TOPIC_PREFIX = "%RETRY%";
    public static final long MASTER_ID = 0L;


    public static String brokerVIPChannel(final boolean isChange, final String brokerAddr) {
        if (isChange) {
            int split = brokerAddr.lastIndexOf(":");
            String ip = brokerAddr.substring(0, split);
            String port = brokerAddr.substring(split + 1);
            String brokerAddrNew = ip + ":" + (Integer.parseInt(port) - 2);
            return brokerAddrNew;
        } else {
            return brokerAddr;
        }
    }
}
