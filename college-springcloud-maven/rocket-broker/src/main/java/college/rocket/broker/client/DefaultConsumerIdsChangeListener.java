package college.rocket.broker.client;

import college.rocket.broker.BrokerController;

/**
 * @author: xuxianbei
 * Date: 2021/3/8
 * Time: 11:14
 * Version:V1.0
 */
public class DefaultConsumerIdsChangeListener implements ConsumerIdsChangeListener {

    private final BrokerController brokerController;

    public DefaultConsumerIdsChangeListener(BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    @Override
    public void handle(ConsumerGroupEvent event, String group, Object... args) {

    }
}
