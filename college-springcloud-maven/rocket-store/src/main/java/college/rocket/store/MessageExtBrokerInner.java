package college.rocket.store;

import college.rocket.common.message.MessageExt;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/1/21
 * Time: 16:22
 * Version:V1.0
 */
@Data
public class MessageExtBrokerInner extends MessageExt {

    private String propertiesString;
}
