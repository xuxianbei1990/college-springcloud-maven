package college.rocket.common.protocol.body;

import college.rocket.remoting.protocol.RemotingSerializable;
import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:45
 * Version:V1.0
 */
@Data
public class RegisterBrokerBody extends RemotingSerializable {
    private TopicConfigSerializeWrapper topicConfigSerializeWrapper = new TopicConfigSerializeWrapper();
    private List<String> filterServerList = new ArrayList<String>();

    public byte[] encode(boolean compress) {
        if (!compress) {
            return super.encode();
        }
        return null;
    }

    public static RegisterBrokerBody decode(byte[] data, boolean compressed) throws IOException {
        if (!compressed) {
            return RegisterBrokerBody.decode(data, RegisterBrokerBody.class);
        }
        return null;
    }
}
