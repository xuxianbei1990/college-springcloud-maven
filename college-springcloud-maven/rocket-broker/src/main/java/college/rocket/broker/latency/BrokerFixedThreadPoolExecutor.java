package college.rocket.broker.latency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:51
 * Version:V1.0
 */
public class BrokerFixedThreadPoolExecutor extends ThreadPoolExecutor {


    public BrokerFixedThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
                                         final TimeUnit unit,
                                         final BlockingQueue<Runnable> workQueue, final ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }
}
