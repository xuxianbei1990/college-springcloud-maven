package college.rocket.common.protocol.body;

import college.rocket.remoting.protocol.RemotingSerializable;
import lombok.Data;

import java.util.HashMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 16:39
 * Version:V1.0
 */
@Data
public class KVTable extends RemotingSerializable {
    private HashMap<String, String> table = new HashMap<String, String>();
}
