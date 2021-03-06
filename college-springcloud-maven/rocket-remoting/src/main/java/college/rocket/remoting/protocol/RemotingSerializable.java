package college.rocket.remoting.protocol;

import com.alibaba.fastjson.JSON;

import java.nio.charset.Charset;

/**
 * @author: xuxianbei
 * Date: 2021/1/7
 * Time: 15:36
 * Version:V1.0
 */
public class RemotingSerializable {

    private final static Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    public static <T> T decode(byte[] body, Class<T> classOfT) {
        final String json = new String(body, CHARSET_UTF8);
        return fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return JSON.parseObject(json, classOfT);
    }

    public byte[] encode() {
        final String json = this.toJson();
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public static String toJson(final Object obj, boolean prettyFormat) {
        return JSON.toJSONString(obj, prettyFormat);
    }

    public static byte[] encode(final Object obj) {
        final String json = toJson(obj, false);
        if (json != null) {
            return json.getBytes(CHARSET_UTF8);
        }
        return null;
    }

    public String toJson() {
        return toJson(false);
    }

    public String toJson(final boolean prettyFormat) {
        return toJson(this, prettyFormat);
    }
}
