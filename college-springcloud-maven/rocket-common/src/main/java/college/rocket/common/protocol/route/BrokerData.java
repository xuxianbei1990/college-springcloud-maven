package college.rocket.common.protocol.route;

import lombok.Data;

import java.util.HashMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/7
 * Time: 16:47
 * Version:V1.0
 */
@Data
public class BrokerData {

    private String brokerName;

    private HashMap<Long/* brokerId */, String/* broker address */> brokerAddrs;
}
