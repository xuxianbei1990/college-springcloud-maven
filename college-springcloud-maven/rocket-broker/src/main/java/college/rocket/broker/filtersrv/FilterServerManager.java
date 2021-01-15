package college.rocket.broker.filtersrv;

import college.rocket.broker.BrokerController;

import java.util.List;

/**
 * @author: xuxianbei
 * Date: 2021/1/14
 * Time: 11:30
 * Version:V1.0
 */
public class FilterServerManager {

    private final BrokerController brokerController;

    public FilterServerManager(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public List<String> buildNewFilterServerList() {
        return null;
    }
}
