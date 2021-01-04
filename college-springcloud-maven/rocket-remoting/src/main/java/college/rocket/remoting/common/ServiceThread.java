package college.rocket.remoting.common;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 14:56
 * Version:V1.0
 */
public abstract class ServiceThread implements Runnable {


    protected final Thread thread;

    public ServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }

    public abstract String getServiceName();

}
