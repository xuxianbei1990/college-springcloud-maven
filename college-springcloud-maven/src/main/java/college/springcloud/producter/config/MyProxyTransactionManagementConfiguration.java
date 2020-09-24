package college.springcloud.producter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.annotation.PostConstruct;

/**
 * 失败无法通过这种方式覆盖原来的类
 * 理由：
 * @author: xuxianbei
 * Date: 2020/9/21
 * Time: 16:20
 * Version:V1.0
 */
//@Configuration(proxyBeanMethods = false)
//@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class MyProxyTransactionManagementConfiguration extends ProxyTransactionManagementConfiguration {
    public MyProxyTransactionManagementConfiguration(ApplicationContext transactionInterceptor) {
        this.applicationContext = transactionInterceptor;
    }

    //    @Bean
//    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
//    public TransactionInterceptor transactionInterceptor(TransactionAttributeSource transactionAttributeSource) {
//        TransactionInterceptor interceptor = new MyTransactionInterceptor();
//        interceptor.setTransactionAttributeSource(transactionAttributeSource);
//        if (this.txManager != null) {
//            interceptor.setTransactionManager(this.txManager);
//        }
//        return interceptor;
//    }

    final ApplicationContext applicationContext;

    @PostConstruct
    public void setIntegerceptor(){
       System.out.println(applicationContext.getBean(TransactionInterceptor.class));
    }
}
