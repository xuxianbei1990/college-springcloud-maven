package college.rocket.store;

import college.rocket.common.BrokerConfig;
import college.rocket.store.config.MessageStoreConfig;
import college.rocket.store.ha.HAService;
import college.rocket.store.stats.BrokerStatsManager;
import lombok.Data;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * @author: xuxianbei
 * Date: 2021/1/23
 * Time: 9:58
 * Version:V1.0
 */
@Data
public class DefaultMessageStore implements MessageStore {

    private final HAService haService;

    private final MessageStoreConfig messageStoreConfig;

    private final AllocateMappedFileService allocateMappedFileService;

    // CommitLog
    private final CommitLog commitLog;

    public DefaultMessageStore(final MessageStoreConfig messageStoreConfig, final BrokerStatsManager brokerStatsManager,
                               final MessageArrivingListener messageArrivingListener, final BrokerConfig brokerConfig) throws IOException {
        this.commitLog = new CommitLog(this);
        this.messageStoreConfig = messageStoreConfig;
        this.allocateMappedFileService = new AllocateMappedFileService(this);

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
}
