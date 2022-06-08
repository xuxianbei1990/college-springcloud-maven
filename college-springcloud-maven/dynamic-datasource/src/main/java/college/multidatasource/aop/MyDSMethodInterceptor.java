package college.multidatasource.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @author: xuxianbei
 * Date: 2021/5/20
 * Time: 17:43
 * Version:V1.0
 */
public class MyDSMethodInterceptor implements MethodInterceptor {


    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        System.out.println("触发了MyDS");
        return invocation.proceed();
    }
}
