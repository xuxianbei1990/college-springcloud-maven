package college.rocket.common.namesrv;

import college.rocket.common.protocol.body.KVTable;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:35
 * Version:V1.0
 */
@Data
public class RegisterBrokerResult {

    private String masterAddr;

    private KVTable kvTable;
}
