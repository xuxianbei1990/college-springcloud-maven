package college.rocketmq.client.impl.consumer;

import college.rocket.remoting.common.ServiceThread;
import college.rocketmq.client.impl.MQConsumerInner;
import college.rocketmq.client.impl.factory.MQClientInstance;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: xuxianbei
 * Date: 2021/1/4
 * Time: 17:21
 * Version:V1.0
 */
@Slf4j
public class PullMessageService extends ServiceThread {

    private final LinkedBlockingQueue<PullRequest> pullRequestQueue = new LinkedBlockingQueue<PullRequest>();
    private final MQClientInstance mQClientFactory;
    private Thread thread;

    public PullMessageService(MQClientInstance mQClientFactory) {
        this.mQClientFactory = mQClientFactory;
    }

    @Override
    public String getServiceName() {
        return PullMessageService.class.getSimpleName();
    }

    protected volatile boolean stopped = false;
    private final AtomicBoolean started = new AtomicBoolean(false);
    protected boolean isDaemon = false;

    @Override
    public void run() {
        log.info(this.getServiceName() + " service started");

        while (!this.isStopped()) {
            try {
                PullRequest pullRequest = this.pullRequestQueue.take();
                this.pullMessage(pullRequest);
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                log.error("Pull Message Service Run Method exception", e);
            }
        }

        log.info(this.getServiceName() + " service end");
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

    public boolean isStopped() {
        return stopped;
    }

    private void pullMessage(final PullRequest pullRequest) {
//        final MQConsumerInner consumer = this.mQClientFactory.selectConsumer(pullRequest.getConsumerGroup());
//        if (consumer != null) {
//            DefaultMQPushConsumerImpl impl = (DefaultMQPushConsumerImpl) consumer;
//            impl.pullMessage(pullRequest);
//        } else {
//            log.warn("No matched consumer for the PullRequest {}, drop it", pullRequest);
//        }
    }
}
