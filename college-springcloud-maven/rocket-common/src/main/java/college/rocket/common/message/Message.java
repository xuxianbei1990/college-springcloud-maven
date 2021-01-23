package college.rocket.common.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: xuxianbei
 * Date: 2020/12/30
 * Time: 18:23
 * Version:V1.0
 */
@Data
public class Message implements Serializable {

    private String topic;

    private byte[] body;

    private int flag;
    private Map<String, String> properties;

    private static final long serialVersionUID = 6917607801604463419L;

    public String getProperty(final String name) {
        if (null == this.properties) {
            this.properties = new HashMap<String, String>();
        }

        return this.properties.get(name);
    }

    void putProperty(final String name, final String value) {
        if (null == this.properties) {
            this.properties = new HashMap<String, String>();
        }

        this.properties.put(name, value);
    }

    public void setInstanceId(String instanceId) {
        this.putProperty(MessageConst.PROPERTY_INSTANCE_ID, instanceId);
    }
}
