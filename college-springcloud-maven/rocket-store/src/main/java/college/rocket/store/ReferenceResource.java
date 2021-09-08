package college.rocket.store;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 11:06
 * Version:V1.0
 */
public abstract class ReferenceResource {

    protected final AtomicLong refCount = new AtomicLong(1);

    protected volatile boolean cleanupOver = false;

    public synchronized boolean hold() {

        return false;
    }

    public void release() {
        long value = this.refCount.decrementAndGet();
        if (value > 0)
            return;

        synchronized (this) {

            this.cleanupOver = this.cleanup(value);
        }
    }

    public abstract boolean cleanup(final long currentRef);
}


