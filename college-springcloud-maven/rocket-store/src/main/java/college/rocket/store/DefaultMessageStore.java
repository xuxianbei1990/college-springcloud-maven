package college.rocket.store;

import college.rocket.common.BrokerConfig;
import college.rocket.remoting.common.ServiceThread;
import college.rocket.store.config.BrokerRole;
import college.rocket.store.config.MessageStoreConfig;
import college.rocket.store.dledger.DLedgerCommitLog;
import college.rocket.store.ha.HAService;
import college.rocket.store.stats.BrokerStatsManager;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.util.concurrent.CompletableFuture;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 9:58
 * Version:V1.0
 */
@Slf4j
@Data
public class DefaultMessageStore implements MessageStore {

    private final HAService haService;

    private final MessageStoreConfig messageStoreConfig;

    private final AllocateMappedFileService allocateMappedFileService;

    private final FlushConsumeQueueService flushConsumeQueueService;

    private final StoreStatsService storeStatsService;

    private volatile boolean shutdown = true;

    private FileLock lock;

    private RandomAccessFile lockFile;

    // CommitLog
    private final CommitLog commitLog;

    public DefaultMessageStore(final MessageStoreConfig messageStoreConfig, final BrokerStatsManager brokerStatsManager,
                               final MessageArrivingListener messageArrivingListener, final BrokerConfig brokerConfig) throws IOException {
        if (messageStoreConfig.isEnableDLegerCommitLog()) {
            this.commitLog = new DLedgerCommitLog(this);
        } else {
            this.commitLog = new CommitLog(this);
        }

        this.messageStoreConfig = messageStoreConfig;
        this.allocateMappedFileService = new AllocateMappedFileService(this);
        this.flushConsumeQueueService = new FlushConsumeQueueService();
        this.storeStatsService = new StoreStatsService();

        if (!messageStoreConfig.isEnableDLegerCommitLog()) {
            this.haService = new HAService(this);
        } else {
            haService = null;
        }
    }


    @Override
    public PutMessageResult putMessage(MessageExtBrokerInner msg) {
        return null;
    }

    @Override
    public long getMaxOffsetInQueue(String topic, int queueId) {
        return 0;
    }

    @Override
    public void updateHaMasterAddress(String newAddr) {
        this.haService.updateMasterAddress(newAddr);
    }


    @Override
    public CompletableFuture<PutMessageResult> asyncPutMessage(MessageExtBrokerInner msg) {
        PutMessageStatus checkStoreStatus = this.checkStoreStatus();
        if (checkStoreStatus != PutMessageStatus.PUT_OK) {
            return CompletableFuture.completedFuture(new PutMessageResult(checkStoreStatus, null));
        }
        CompletableFuture<PutMessageResult> putResultFuture = this.commitLog.asyncPutMessage(msg);
        return putResultFuture;
    }

    private PutMessageStatus checkStoreStatus() {
        return PutMessageStatus.PUT_OK;
    }

    public long getMaxPhyOffset() {
        return this.commitLog.getMaxOffset();
    }

    public boolean appendToCommitLog(long startOffset, byte[] data) {
        if (this.shutdown) {
            log.warn("message store has shutdown, so appendToPhyQueue is forbidden");
            return false;
        }
//        boolean result = this.commitLog.appendData(startOffset, data);

        return true;
    }

    @Override
    public void start() throws Exception {
        //加锁
        lock = lockFile.getChannel().tryLock(0, 1, false);
        if (lock == null || lock.isShared() || !lock.isValid()) {
            throw new RuntimeException("Lock failed,MQ already started");
        }

        lockFile.getChannel().write(ByteBuffer.wrap("lock".getBytes()));
        lockFile.getChannel().force(true);

        if (!messageStoreConfig.isEnableDLegerCommitLog()) {
            //实现服务端代码，也实现了客户端代码。通过NIO通信
            this.haService.start();
            this.handleScheduleMessageService(messageStoreConfig.getBrokerRole());
        }
        //其实就是把ConsumerQueue 刷到磁盘上
        this.flushConsumeQueueService.start();
        this.commitLog.start();
        this.storeStatsService.start();

        this.createTempFile();
        this.addScheduleTask();
        this.shutdown = false;

    }

    private void addScheduleTask() {

    }

    private void createTempFile() {

    }

    class FlushConsumeQueueService extends ServiceThread {
        private static final int RETRY_TIMES_OVER = 3;

        @Override
        public String getServiceName() {
            return null;
        }

        @Override
        public void run() {
            DefaultMessageStore.log.info(this.getServiceName() + " service started");
            while (!this.isStopped()) {
                int interval = DefaultMessageStore.this.getMessageStoreConfig().getFlushIntervalConsumeQueue();
                this.waitForRunning(interval);
                this.doFlush(1);
            }
        }

        private void doFlush(int retryTimes) {
            int flushConsumeQueueLeastPages = DefaultMessageStore.this.getMessageStoreConfig().getFlushConsumeQueueLeastPages();
            if (retryTimes == RETRY_TIMES_OVER) {
                flushConsumeQueueLeastPages = 0;
            }

            long logicsMsgTimestamp = 0;

            int flushConsumeQueueThoroughInterval = DefaultMessageStore.this.getMessageStoreConfig().getFlushConsumeQueueThoroughInterval();
        }
    }

    private void handleScheduleMessageService(BrokerRole brokerRole) {
//        if (this.scheduleMessageService != null) {
//            if (brokerRole == BrokerRole.SLAVE) {
//                this.scheduleMessageService.shutdown();
//            } else {
//                this.scheduleMessageService.start();
//            }
//        }
    }
}
