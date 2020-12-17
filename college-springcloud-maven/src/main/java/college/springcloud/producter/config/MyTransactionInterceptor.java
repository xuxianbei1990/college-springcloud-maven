package college.springcloud.producter.config;

import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * 失败，详见@see #MyProxyTransactionManagementConfiguration
 * @author: xuxianbei
 * Date: 2020/9/21
 * Time: 16:21
 * Version:V1.0
 */
public class MyTransactionInterceptor extends TransactionInterceptor {

    @Override
    protected void completeTransactionAfterThrowing(TransactionInfo txInfo, Throwable ex) {
        System.out.println("插入回滚事务成功");
        super.completeTransactionAfterThrowing(txInfo, ex);
    }
}
