package college.rocket.store;

import college.rocket.remoting.common.ServiceThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: xuxianbei
 * Date: 2021/3/19
 * Time: 13:43
 * Version:V1.0
 */
public class StoreStatsService extends ServiceThread {
    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void run() {

    }

    public AtomicInteger getSinglePutMessageTopicTimesTotal(String topic) {
        return null;
    }

    public AtomicInteger getSinglePutMessageTopicSizeTotal(String topic) {
        return null;
    }
}
