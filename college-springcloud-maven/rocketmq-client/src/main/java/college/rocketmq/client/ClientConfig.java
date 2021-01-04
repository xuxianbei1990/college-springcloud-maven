package college.rocketmq.client;

import college.rocket.remoting.common.RemotingUtil;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 15:49
 * Version:V1.0
 */
public class ClientConfig {

    private String namesrvAddr = "localhost:9876";/*NameServerAddressUtils.getNameServerAddresses();*/

    private String clientIP = RemotingUtil.getLocalAddress();

    public ClientConfig cloneClientConfig() {
        ClientConfig cc = new ClientConfig();
        cc.namesrvAddr = namesrvAddr;
        cc.clientIP = clientIP;
        return cc;
    }
}
