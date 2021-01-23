package college.rocket.broker.longpolling;

import college.rocket.broker.BrokerController;
import college.rocket.remoting.common.ServiceThread;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 10:11
 * Version:V1.0
 */
public class PullRequestHoldService extends ServiceThread {

    private final BrokerController brokerController;

    public PullRequestHoldService(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void run() {

    }
}
