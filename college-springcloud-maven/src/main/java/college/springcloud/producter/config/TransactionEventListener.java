package college.springcloud.producter.config;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 事务监听
 * @author: xuxianbei
 * Date: 2020/9/21
 * Time: 16:53
 * Version:V1.0
 */
@Component
public class TransactionEventListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listenCommit(MyTransactionEvent event) {
        System.out.println("事务提交执行:" + event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void listenRollBack(MyTransactionEvent event) {
        System.out.println("事务回滚执行:" + event);
    }
}
