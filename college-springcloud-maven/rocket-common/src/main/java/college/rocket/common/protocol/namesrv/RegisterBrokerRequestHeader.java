package college.rocket.common.protocol.namesrv;

import college.rocket.remoting.CommandCustomHeader;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:43
 * Version:V1.0
 */
@Data
public class RegisterBrokerRequestHeader implements CommandCustomHeader {

    private String brokerName;

    private String brokerAddr;

    private Long brokerId;

    private String clusterName;

    private Integer bodyCrc32 = 0;

    private boolean compressed;
}
