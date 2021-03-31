package college.rocket.store.dledger;

import college.rocket.common.UtilAll;
import college.rocket.common.message.MessageDecoder;
import college.rocket.common.sysflag.MessageSysFlag;
import college.rocket.store.*;
import io.openmessaging.storage.dledger.AppendFuture;
import io.openmessaging.storage.dledger.DLedgerConfig;
import io.openmessaging.storage.dledger.DLedgerServer;
import io.openmessaging.storage.dledger.entry.DLedgerEntry;
import io.openmessaging.storage.dledger.protocol.AppendEntryRequest;
import io.openmessaging.storage.dledger.protocol.AppendEntryResponse;
import io.openmessaging.storage.dledger.protocol.DLedgerResponseCode;
import io.openmessaging.storage.dledger.store.file.DLedgerMmapFileStore;
import io.openmessaging.storage.dledger.store.file.MmapFileList;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/3/29
 * Time: 9:39
 * Version:V1.0
 */

public class DLedgerCommitLog extends CommitLog {
    private DLedgerConfig dLedgerConfig;
    private DLedgerServer dLedgerServer;
    private final DLedgerMmapFileStore dLedgerFileStore;
    private final MmapFileList dLedgerFileList;
    private final MessageSerializer messageSerializer;
    private volatile long beginTimeInDledgerLock = 0;


    public DLedgerCommitLog(DefaultMessageStore defaultMessageStore) {
        super(defaultMessageStore);
        dLedgerConfig = new DLedgerConfig();
        //是否开启硬盘强制清除
        dLedgerConfig.setEnableDiskForceClean(defaultMessageStore.getMessageStoreConfig().isCleanFileForciblyEnable());
        //日志存储类型
        dLedgerConfig.setStoreType(DLedgerConfig.FILE);
        //自己的id
        dLedgerConfig.setSelfId(defaultMessageStore.getMessageStoreConfig().getDLegerSelfId());
        dLedgerConfig.setGroup(defaultMessageStore.getMessageStoreConfig().getDLegerGroup());
        dLedgerConfig.setPeers(defaultMessageStore.getMessageStoreConfig().getDLegerPeers());
        dLedgerConfig.setStoreBaseDir(defaultMessageStore.getMessageStoreConfig().getStorePathRootDir());
        dLedgerConfig.setMappedFileSizeForEntryData(defaultMessageStore.getMessageStoreConfig().getMappedFileSizeCommitLog());
        dLedgerConfig.setDeleteWhen(defaultMessageStore.getMessageStoreConfig().getDeleteWhen());
        dLedgerConfig.setFileReservedHours(defaultMessageStore.getMessageStoreConfig().getFileReservedTime() + 1);
        dLedgerServer = new DLedgerServer(dLedgerConfig);
        dLedgerFileStore = (DLedgerMmapFileStore) dLedgerServer.getdLedgerStore();
        DLedgerMmapFileStore.AppendHook appendHook = (entry, buffer, bodyOffset) -> {
            dLedgerServer = new DLedgerServer(dLedgerConfig);
            buffer.position(buffer.position() + bodyOffset + MessageDecoder.PHY_POS_POSITION);
            buffer.putLong(entry.getPos() + bodyOffset);
        };
        dLedgerFileStore.addAppendHook(appendHook);
        dLedgerFileList = dLedgerFileStore.getDataFileList();
        this.messageSerializer = new MessageSerializer(defaultMessageStore.getMessageStoreConfig().getMaxMessageSize());
    }

    @Override
    public void start() {
        dLedgerServer.startup();
    }

    @Override
    public void shutdown() {
        dLedgerServer.shutdown();
    }

    @Override
    public CompletableFuture<PutMessageResult> asyncPutMessage(MessageExtBrokerInner msg) {
        return CompletableFuture.completedFuture(this.putMessage(msg));
    }

    @Override
    public PutMessageResult putMessage(final MessageExtBrokerInner msg) {
        msg.setStoreTimestamp(System.currentTimeMillis());
        msg.setBodyCRC(UtilAll.crc32(msg.getBody()));
        StoreStatsService storeStatsService = this.defaultMessageStore.getStoreStatsService();
        String topic = msg.getTopic();

        // Back to Results
        AppendMessageResult appendResult = null;
        AppendFuture<AppendEntryResponse> dledgerFuture = null;
        EncodeResult encodeResult;
        putMessageLock.lock();
        long elapsedTimeInLock;
        long queueOffset;
        try {
            beginTimeInDledgerLock = System.currentTimeMillis();
            encodeResult = this.messageSerializer.serialize(msg);
            queueOffset = topicQueueTable.get(encodeResult.queueOffsetKey);
            if (encodeResult.status != AppendMessageStatus.PUT_OK) {
                return new PutMessageResult(PutMessageStatus.MESSAGE_ILLEGAL, new AppendMessageResult(encodeResult.status));
            }
            //组织需要发送其他节点数据
            AppendEntryRequest request = new AppendEntryRequest();
            request.setGroup(dLedgerConfig.getGroup());
            request.setRemoteId(dLedgerServer.getMemberState().getSelfId());
            request.setBody(encodeResult.data);
            //这里应该是日志同步. 这里是异步实现的
            dledgerFuture = (AppendFuture<AppendEntryResponse>) dLedgerServer.handleAppend(request);
            if (dledgerFuture.getPos() == -1) {
                return new PutMessageResult(PutMessageStatus.OS_PAGECACHE_BUSY, new AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR));
            }
            long wroteOffset = dledgerFuture.getPos() + DLedgerEntry.BODY_OFFSET;
            int msgIdLength = (msg.getSysFlag() & MessageSysFlag.STOREHOSTADDRESS_V6_FLAG) == 0 ? 4 + 4 + 8 : 16 + 4 + 8;
            ByteBuffer buffer = ByteBuffer.allocate(msgIdLength);
            String msgId = MessageDecoder.createMessageId(buffer, msg.getStoreHostBytes(), wroteOffset);
            elapsedTimeInLock = System.currentTimeMillis() - beginTimeInDledgerLock;
            appendResult = new AppendMessageResult(AppendMessageStatus.PUT_OK, wroteOffset, encodeResult.data.length, msgId, System.currentTimeMillis(), queueOffset, elapsedTimeInLock);
        } catch (Exception e) {

        }
        PutMessageStatus putMessageStatus = PutMessageStatus.UNKNOWN_ERROR;
        try {
            //这里说明一个问题，这里是拿到了节点回应，然后返回的。
            AppendEntryResponse appendEntryResponse = dledgerFuture.get(3, TimeUnit.SECONDS);
            switch (DLedgerResponseCode.valueOf(appendEntryResponse.getCode())) {
                case SUCCESS:
                    putMessageStatus = PutMessageStatus.PUT_OK;
                case INCONSISTENT_LEADER:
                case NOT_LEADER:
                case LEADER_NOT_READY:
                case DISK_FULL:
                    putMessageStatus = PutMessageStatus.SERVICE_NOT_AVAILABLE;
                    break;
                case WAIT_QUORUM_ACK_TIMEOUT:
                    //Do not return flush_slave_timeout to the client, for the ons client will ignore it.
                    putMessageStatus = PutMessageStatus.OS_PAGECACHE_BUSY;
                    break;
                case LEADER_PENDING_FULL:
                    putMessageStatus = PutMessageStatus.OS_PAGECACHE_BUSY;
                    break;
            }
        } catch (Exception e) {

        }

        PutMessageResult putMessageResult = new PutMessageResult(putMessageStatus, appendResult);
        if (putMessageStatus == PutMessageStatus.PUT_OK) {
            // Statistics
            storeStatsService.getSinglePutMessageTopicTimesTotal(msg.getTopic()).incrementAndGet();
            storeStatsService.getSinglePutMessageTopicSizeTotal(topic).addAndGet(appendResult.getWroteBytes());
        }
        return putMessageResult;

    }

    class EncodeResult {
        private String queueOffsetKey;
        private byte[] data;
        private AppendMessageStatus status;

        public EncodeResult(AppendMessageStatus status, byte[] data, String queueOffsetKey) {
            this.data = data;
            this.status = status;
            this.queueOffsetKey = queueOffsetKey;
        }
    }


    class MessageSerializer {

        private static final int END_FILE_MIN_BLANK_LENGTH = 4 + 4;

        private final ByteBuffer msgIdMemory;
        private final ByteBuffer msgIdV6Memory;
        // Store the message content
        private final ByteBuffer msgStoreItemMemory;
        // The maximum length of the message
        private final int maxMessageSize;

        MessageSerializer(final int size) {
            this.msgIdMemory = ByteBuffer.allocate(4 + 4 + 8);
            this.msgIdV6Memory = ByteBuffer.allocate(16 + 4 + 8);
            this.msgStoreItemMemory = ByteBuffer.allocate(size + END_FILE_MIN_BLANK_LENGTH);
            this.maxMessageSize = size;
        }

        public EncodeResult serialize(final MessageExtBrokerInner msgInner) {
            return null;
        }
    }

    @Override
    public long flush() {
        dLedgerFileStore.flush();
        return dLedgerFileList.getFlushedWhere();
    }

    @Override
    public boolean load() {
        return super.load();
    }

    public DLedgerConfig getdLedgerConfig() {
        return dLedgerConfig;
    }

    public void setdLedgerConfig(DLedgerConfig dLedgerConfig) {
        this.dLedgerConfig = dLedgerConfig;
    }

    public DLedgerServer getdLedgerServer() {
        return dLedgerServer;
    }

    public void setdLedgerServer(DLedgerServer dLedgerServer) {
        this.dLedgerServer = dLedgerServer;
    }
}
