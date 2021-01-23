package college.rocket.broker.longpolling;

import college.rocket.store.MessageArrivingListener;

import java.util.Map;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 10:10
 * Version:V1.0
 */
public class NotifyMessageArrivingListener implements MessageArrivingListener {

    private final PullRequestHoldService pullRequestHoldService;

    public NotifyMessageArrivingListener(final PullRequestHoldService pullRequestHoldService) {
        this.pullRequestHoldService = pullRequestHoldService;
    }


    @Override
    public void arriving(String topic, int queueId, long logicOffset, long tagsCode, long msgStoreTime, byte[] filterBitMap, Map<String, String> properties) {

    }
}
