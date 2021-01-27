package college.rocket.store;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:06
 * Version:V1.0
 */
public abstract class ReferenceResource {

    public synchronized boolean hold() {

        return false;
    }
}


