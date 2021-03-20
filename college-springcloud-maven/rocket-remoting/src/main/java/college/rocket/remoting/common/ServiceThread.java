package college.rocket.remoting.common;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 14:56
 * Version:V1.0
 */
@Slf4j
public abstract class ServiceThread implements Runnable {

    protected volatile boolean stopped = false;
    protected volatile AtomicBoolean hasNotified = new AtomicBoolean(false);
    private final AtomicBoolean started = new AtomicBoolean(false);
    //    protected final CountDownLatch2 waitPoint = new CountDownLatch2(1);
    protected boolean isDaemon = false;
    protected Thread thread;

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

    public void start() {
        log.info("Try to start service thread:{} started:{} lastThread:{}", getServiceName(), started.get(), thread);
        if (!started.compareAndSet(false, true)) {
            return;
        }
        stopped = false;
        this.thread = new Thread(this, getServiceName());
        this.thread.setDaemon(isDaemon);
        this.thread.start();
    }

    public void setDaemon(boolean daemon) {
        isDaemon = daemon;
    }

}
