package college.rocket.common.message;

import java.util.Map;

/**
 * @author: xuxianbei
 * Date: 2021/1/19
 * Time: 16:10
 * Version:V1.0
 */
public class MessageDecoder {

    public static final char NAME_VALUE_SEPARATOR = 1;

    public static final char PROPERTY_SEPARATOR = 2;

    public static String messageProperties2String(Map<String, String> properties) {
        StringBuilder sb = new StringBuilder();
        if (properties != null) {
            for (final Map.Entry<String, String> entry : properties.entrySet()) {
                final String name = entry.getKey();
                final String value = entry.getValue();

                if (value == null) {
                    continue;
                }
                sb.append(name);
                sb.append(NAME_VALUE_SEPARATOR);
                sb.append(value);
                sb.append(PROPERTY_SEPARATOR);
            }
        }
        return sb.toString();
    }
}
