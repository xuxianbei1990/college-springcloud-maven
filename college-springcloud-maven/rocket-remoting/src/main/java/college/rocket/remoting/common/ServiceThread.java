package college.rocket.remoting.common;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 14:56
 * Version:V1.0
 */
public abstract class ServiceThread implements Runnable {

    protected volatile boolean stopped = false;
    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
//    protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);

    protected final Thread thread;

    public ServiceThread() {
        this.thread = new Thread(this, this.getServiceName());
    }

    public abstract String getServiceName();

    public void wakeup() {
        if (hasNotified.compareAndSet(false, true)) {
//            waitPoint.countDown(); // notify
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    protected void waitForRunning(long interval) {

    }

}
