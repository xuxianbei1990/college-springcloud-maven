package college.multidatasource.config;

import college.multidatasource.aop.MyDSAnnotationAdvisor;
import college.multidatasource.aop.MyDSMethodInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: xuxianbei
 * Date: 2021/5/20
 * Time: 17:51
 * Version:V1.0
 */
@Configuration
public class MyDataSourceConfiguration {


    @Bean
    public MyDSMethodInterceptor myDSMethodInterceptor() {
        return new MyDSMethodInterceptor();
    }

    @Bean
    public MyDSAnnotationAdvisor myDSAnnotationAdvisor(MyDSMethodInterceptor myDSMethodInterceptor) {
        return new MyDSAnnotationAdvisor(myDSMethodInterceptor);
    }
}
