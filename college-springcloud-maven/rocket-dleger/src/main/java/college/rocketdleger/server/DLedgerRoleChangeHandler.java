package college.rocketdleger.server;

import io.openmessaging.storage.dledger.DLedgerLeaderElector;
import io.openmessaging.storage.dledger.DLedgerServer;
import io.openmessaging.storage.dledger.MemberState;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用于接收Dledger
 *
 * @author: xuxianbei
 * Date: 2021/3/30
 * Time: 17:25
 * Version:V1.0
 */
@Slf4j
public class DLedgerRoleChangeHandler implements DLedgerLeaderElector.RoleChangeHandler {
    private ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryImpl("DLegerRoleChangeHandler_"));

    private DLedgerLog dLedgerLog;
    private DLedgerServer dLegerServer;

    public DLedgerRoleChangeHandler(DLedgerLog dLedgerLog) {
        this.dLedgerLog = dLedgerLog;
        this.dLegerServer = dLedgerLog.getDLedgerServer();
    }

    /**
     * 这个写法很有意思：好处就是把同步变成异步了
     * 而且是不用改原始接口
     *
     * @param term
     * @param role
     */
    @Override
    public void handle(long term, MemberState.Role role) {
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                long start = System.currentTimeMillis();
                try {
                    boolean succ = true;
                    log.info("Begin handling broker role change term={} role={} currStoreRole={}", term, role, "");
                    switch (role) {
                        case CANDIDATE:
                            break;
                        case FOLLOWER:
                            break;
                        case LEADER:
                            while (true) {
                                if (!dLegerServer.getMemberState().isLeader()) {
                                    succ = false;
                                    break;
                                }
                                if (dLegerServer.getdLedgerStore().getLedgerEndIndex() == -1) {
                                    break;
                                }
                                if (dLegerServer.getdLedgerStore().getLedgerEndIndex() == dLegerServer.getdLedgerStore().getCommittedIndex()){
                                    break;
                                }
                                Thread.sleep(100);
                            }
                            if (succ) {
//                                messageStore.recoverTopicQueueTable();
//                                brokerController.changeToMaster(BrokerRole.SYNC_MASTER);
                            }
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    System.out.println("exception e");
                }

            }
        };
        executorService.submit(runnable);
    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }
}
