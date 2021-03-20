package college.rocket.common;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:54
 * Version:V1.0
 */
public abstract class ConfigManager {

    public synchronized void persist() {
        String jsonString = this.encode(true);
        //就是把Topic 序列化本地磁盘上了
    }

    public abstract String encode(final boolean prettyFormat);
}
