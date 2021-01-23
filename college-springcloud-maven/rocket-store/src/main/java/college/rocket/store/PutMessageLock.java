package college.rocket.store;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:22
 * Version:V1.0
 */
public interface PutMessageLock {
    void lock();

    void unlock();
}
