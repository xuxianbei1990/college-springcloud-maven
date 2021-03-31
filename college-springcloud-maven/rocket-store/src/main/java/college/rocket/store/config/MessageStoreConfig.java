package college.rocket.store.config;

import lombok.Data;

import java.io.File;

/**
 * @author: xuxianbei
 * Date: 2021/1/13
 * Time: 17:29
 * Version:V1.0
 */
@Data
public class MessageStoreConfig {

    private FlushDiskType flushDiskType = FlushDiskType.ASYNC_FLUSH;
    private boolean useReentrantLockWhenPutMessage = false;

    private int flushIntervalConsumeQueue = 1000;

    private int flushConsumeQueueThoroughInterval = 1000 * 60;

    //删除时间
    private String deleteWhen = "04";

    //文件预留时间
    private int fileReservedTime = 72;

    private String dLegerSelfId;

    private String dLegerGroup;
    private String dLegerPeers;

    private String storePathRootDir = System.getProperty("user.home") + File.separator + "store";

    private int flushConsumeQueueLeastPages = 2;

    private int haSendHeartbeatInterval = 1000 * 5;

    private int haHousekeepingInterval = 1000 * 20;

    private int haListenPort = 10912;

    private int mappedFileSizeCommitLog = 1024 * 1024 * 1024;

    private int maxMessageSize = 1024 * 1024 * 4;

    private boolean cleanFileForciblyEnable = true;

    private String storePathCommitLog = System.getProperty("user.home") + File.separator + "store"
            + File.separator + "commitlog";

    private boolean flushCommitLogTimed = false;

    private int flushCommitLogThoroughInterval = 1000 * 10;

    // CommitLog flush interval 异步刷盘间隔
    // flush data to disk
    private int flushIntervalCommitLog = 500;

    private int flushCommitLogLeastPages = 4;

    private boolean enableDLegerCommitLog = false;

    private BrokerRole brokerRole = BrokerRole.ASYNC_MASTER;

}
