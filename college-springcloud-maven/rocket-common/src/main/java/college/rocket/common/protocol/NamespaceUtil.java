package college.rocket.common.protocol;

import org.springframework.util.StringUtils;

/**
 * @author: xuxianbei
 * Date: 2021/1/18
 * Time: 16:04
 * Version:V1.0
 */
public class NamespaceUtil {

    public static String wrapNamespace(String namespace, String resourceWithOutNamespace) {
        if (StringUtils.isEmpty(namespace) || StringUtils.isEmpty(resourceWithOutNamespace)) {
            return resourceWithOutNamespace;
        }
        return "";
    }

    public static String withoutNamespace(String resourceWithNamespace, String namespace) {
        if (StringUtils.isEmpty(resourceWithNamespace) || StringUtils.isEmpty(namespace)) {
            return resourceWithNamespace;
        }
        return null;
    }
}
