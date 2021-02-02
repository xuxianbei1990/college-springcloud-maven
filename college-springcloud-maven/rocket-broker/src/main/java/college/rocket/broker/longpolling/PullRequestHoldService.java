package college.rocket.broker.longpolling;

import college.rocket.broker.BrokerController;
import college.rocket.remoting.common.ServiceThread;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 10:11
 * Version:V1.0
 */
@Slf4j
public class PullRequestHoldService extends ServiceThread {

    private final BrokerController brokerController;
    private static final String TOPIC_QUEUEID_SEPARATOR = "@";
    private ConcurrentMap<String/* topic@queueId */, ManyPullRequest> pullRequestTable =
            new ConcurrentHashMap<String, ManyPullRequest>(1024);

    public PullRequestHoldService(final BrokerController brokerController) {
        this.brokerController = brokerController;
    }

    public void suspendPullRequest(final String topic, final int queueId, final PullRequest pullRequest) {
        String key = this.buildKey(topic, queueId);
        ManyPullRequest mpr = this.pullRequestTable.get(key);
        if (null == mpr) {
            mpr = new ManyPullRequest();
            ManyPullRequest prev = this.pullRequestTable.putIfAbsent(key, mpr);
            if (prev != null) {
                mpr = prev;
            }
        }
        mpr.addPullRequest(pullRequest);
    }

    private String buildKey(final String topic, final int queueId) {
        StringBuilder sb = new StringBuilder();
        sb.append(topic);
        sb.append(TOPIC_QUEUEID_SEPARATOR);
        sb.append(queueId);
        return sb.toString();
    }

    @Override
    public String getServiceName() {
        return null;
    }

    @Override
    public void run() {
        log.info("{} service started", this.getServiceName());
        while (!this.isStopped()) {
            if (this.brokerController.getBrokerConfig().isLongPollingEnable()) {
                this.waitForRunning(5 * 1000);
            }

            long beginLockTimestamp = System.currentTimeMillis();
            this.checkHoldRequest();
            long costTime = System.currentTimeMillis() - beginLockTimestamp;
            if (costTime > 5 * 1000) {
                log.info("[NOTIFYME] check hold request cost {} ms.", costTime);
            }

        }
    }

    private void checkHoldRequest() {

    }
}
