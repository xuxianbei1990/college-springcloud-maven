package college.rocketdleger.server;

import io.openmessaging.storage.dledger.AppendFuture;
import io.openmessaging.storage.dledger.DLedgerConfig;
import io.openmessaging.storage.dledger.DLedgerServer;
import io.openmessaging.storage.dledger.entry.DLedgerEntry;
import io.openmessaging.storage.dledger.protocol.AppendEntryRequest;
import io.openmessaging.storage.dledger.protocol.AppendEntryResponse;
import io.openmessaging.storage.dledger.protocol.DLedgerResponseCode;
import io.openmessaging.storage.dledger.store.file.DLedgerMmapFileStore;
import io.openmessaging.storage.dledger.store.file.MmapFileList;
import lombok.Data;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author: xuxianbei
 * Date: 2021/3/30
 * Time: 9:40
 * Version:V1.0
 */
@Data
public class DLedgerLog {

    /**
     * 这里的n0对应的就是selfId
     */
    private static final String PEERS = "n0-127.0.0.1:40911;n1-127.0.0.1:40912;n2-127.0.0.1:40913";

    private String storePathRootDir = System.getProperty("user.home") + File.separator + "store" + File.separator;
    public static final int PHY_POS_POSITION = 4 + 4 + 4 + 4 + 4 + 8;


    private final DLedgerServer dLedgerServer;
    private final DLedgerConfig dLedgerConfig;

    private final DLedgerMmapFileStore dLedgerFileStore;
    private final MmapFileList dLedgerFileList;

    public DLedgerLog(String selfId) {
        dLedgerConfig = new DLedgerConfig();
        //是否强制删除文件
        dLedgerConfig.setEnableDiskForceClean(true);
        //以文件方式存储
        dLedgerConfig.setStoreType(DLedgerConfig.FILE);
        dLedgerConfig.setSelfId(selfId);
        dLedgerConfig.setGroup("XXB");
        dLedgerConfig.setPeers(PEERS);
        dLedgerConfig.setStoreBaseDir(storePathRootDir + selfId);
        dLedgerConfig.setDeleteWhen("04");
        dLedgerConfig.setFileReservedHours(72);
        dLedgerServer = new DLedgerServer(dLedgerConfig);
        dLedgerFileStore = (DLedgerMmapFileStore) dLedgerServer.getdLedgerStore();

//        DLedgerMmapFileStore.AppendHook appendHook = (entry, buffer, bodyOffset) -> {
//            assert bodyOffset == DLedgerEntry.BODY_OFFSET;
//            buffer.position(buffer.position() + bodyOffset + PHY_POS_POSITION);
//            buffer.putLong(entry.getPos() + bodyOffset);
//        };
//        dLedgerFileStore.addAppendHook(appendHook);
        dLedgerFileList = dLedgerFileStore.getDataFileList();
    }

    public void start() {
        dLedgerServer.startup();
    }

    public void shutdown() {
        dLedgerServer.shutdown();
    }


    public void appendHandle() throws Exception {
        AppendEntryRequest request = new AppendEntryRequest();
        request.setGroup(dLedgerConfig.getGroup());
        request.setRemoteId(dLedgerServer.getMemberState().getSelfId());
        request.setBody("super 贝吉塔".getBytes());
        AppendFuture<AppendEntryResponse> dledgerFuture = (AppendFuture<AppendEntryResponse>) dLedgerServer.handleAppend(request);
//        if (dledgerFuture.getPos() == -1) {
//            throw new RuntimeException("未知错误异常");
//        }

        AppendEntryResponse appendEntryResponse = dledgerFuture.get(3, TimeUnit.SECONDS);
        switch (DLedgerResponseCode.valueOf(appendEntryResponse.getCode())) {
            case SUCCESS:
                System.out.println(DLedgerResponseCode.SUCCESS);
                break;
            case INCONSISTENT_LEADER:
            case NOT_LEADER:
            case LEADER_NOT_READY:
            case DISK_FULL:
                System.out.println(DLedgerResponseCode.DISK_FULL);
                break;
            case WAIT_QUORUM_ACK_TIMEOUT:
                //Do not return flush_slave_timeout to the client, for the ons client will ignore it.
                System.out.println(DLedgerResponseCode.WAIT_QUORUM_ACK_TIMEOUT);
                break;
            case LEADER_PENDING_FULL:
                System.out.println(DLedgerResponseCode.LEADER_PENDING_FULL);
                break;
        }
        System.out.println(appendEntryResponse);
    }
}
