package college.rocket.common;

import college.rocket.remoting.protocol.RemotingSerializable;
import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xuxianbei
 * Date: 2021/1/15
 * Time: 17:08
 * Version:V1.0
 */
@Data
public class DataVersion extends RemotingSerializable {
    private long timestamp = System.currentTimeMillis();
    private AtomicLong counter = new AtomicLong(0);
}