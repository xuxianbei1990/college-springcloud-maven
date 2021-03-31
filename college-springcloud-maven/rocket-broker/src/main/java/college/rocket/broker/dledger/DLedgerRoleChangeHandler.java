package college.rocket.broker.dledger;

import college.rocket.broker.BrokerController;
import college.rocket.store.DefaultMessageStore;
import college.rocket.store.dledger.DLedgerCommitLog;
import io.openmessaging.storage.dledger.DLedgerLeaderElector;
import io.openmessaging.storage.dledger.DLedgerServer;
import io.openmessaging.storage.dledger.MemberState;
import lombok.Data;

/**
 * @author: xuxianbei
 * Date: 2021/3/25
 * Time: 11:26
 * Version:V1.0
 */

public class DLedgerRoleChangeHandler implements DLedgerLeaderElector.RoleChangeHandler {

    private BrokerController brokerController;
    private DefaultMessageStore messageStore;
    private DLedgerCommitLog dLedgerCommitLog;
    private DLedgerServer dLegerServer;

    public DLedgerRoleChangeHandler(BrokerController brokerController, DefaultMessageStore messageStore) {
        this.brokerController = brokerController;
        this.messageStore = messageStore;
        this.dLedgerCommitLog = (DLedgerCommitLog) messageStore.getCommitLog();
        this.dLegerServer = dLedgerCommitLog.getdLedgerServer();
    }

    @Override
    public void handle(long l, MemberState.Role role) {

    }

    @Override
    public void startup() {

    }

    @Override
    public void shutdown() {

    }
}
