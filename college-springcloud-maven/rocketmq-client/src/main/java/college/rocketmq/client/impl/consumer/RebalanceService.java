package college.rocketmq.client.impl.consumer;

import college.rocket.remoting.common.ServiceThread;
import college.rocketmq.client.impl.factory.MQClientInstance;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: xuxianbei
 * Date: 2021/1/27
 * Time: 18:15
 * Version:V1.0
 */
@Slf4j
public class RebalanceService extends ServiceThread {

    private static long waitInterval = 20000;
    private final MQClientInstance mqClientFactory;

    public RebalanceService(MQClientInstance mqClientInstance) {
        this.mqClientFactory = mqClientInstance;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void run() {
        log.info(this.getServiceName() + " service started");
        while (!this.isStopped()) {
            this.waitForRunning(waitInterval);
            this.mqClientFactory.doRebalance();
        }
        log.info(this.getServiceName() + " service end");
    }
}
