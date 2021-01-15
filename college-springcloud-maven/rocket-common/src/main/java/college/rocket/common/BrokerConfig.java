package college.rocket.common;

import college.rocket.remoting.common.RemotingUtil;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:27
 * Version:V1.0
 */
@Data
public class BrokerConfig {

    /**
     * This configurable item defines interval of topics registration of broker to name server. Allowing values are
     * between 10, 000 and 60, 000 milliseconds.
     */
    private int registerNameServerPeriod = 1000 * 30;

    private boolean forceRegister = true;

    private String brokerClusterName = "DefaultCluster";

    private String brokerIP1 = RemotingUtil.getLocalAddress();

    private String brokerIP2 = RemotingUtil.getLocalAddress();

    private String brokerName = "DEFAULT_BROKER";

    private long brokerId = MixAll.MASTER_ID;

    private int registerBrokerTimeoutMills = 6000;

    private boolean compressedRegister = false;

    private String namesrvAddr = "localhost:9876";
}
