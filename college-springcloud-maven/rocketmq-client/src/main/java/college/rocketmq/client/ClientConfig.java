package college.rocketmq.client;

import college.rocket.remoting.common.RemotingUtil;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 15:49
 * Version:V1.0
 */
@Data
public class ClientConfig {

    private String namesrvAddr = "localhost:9876";/*NameServerAddressUtils.getNameServerAddresses();*/

    private String clientIP = RemotingUtil.getLocalAddress();

    protected String namespace;

    private int pollNameServerInterval = 1000 * 30;

    public ClientConfig cloneClientConfig() {
        ClientConfig cc = new ClientConfig();
        cc.namesrvAddr = namesrvAddr;
        cc.clientIP = clientIP;
        return cc;
    }
}
