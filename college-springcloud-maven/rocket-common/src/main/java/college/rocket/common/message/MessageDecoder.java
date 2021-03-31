package college.rocket.common.message;

import college.rocket.common.UtilAll;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
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

    public static final int PHY_POS_POSITION = 4 + 4 + 4 + 4 + 4 + 8;

    public final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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

    public static String createMessageId(final ByteBuffer input, final ByteBuffer addr, final long offset) {
        input.flip();
        int msgIDLength = addr.limit() == 8 ? 16 : 28;
        input.limit(msgIDLength);
        input.put(addr);
        input.putLong(offset);

        return UtilAll.bytes2string(input.array());
    }
}
