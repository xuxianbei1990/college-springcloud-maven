package college.rocket.common;

/**
 * @author: xuxianbei
 * Date: 2021/1/8
 * Time: 11:34
 * Version:V1.0
 */
public class Configuration {

    public Configuration(Object... configObjects) {
        if (configObjects == null || configObjects.length == 0) {
            return;
        }
        for (Object configObject : configObjects) {
//            registerConfig(configObject);
        }
    }
}
