package college.multidatasource.aop;

import college.multidatasource.annotation.MyDS;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * @author: xuxianbei
 * Date: 2021/5/20
 * Time: 17:26
 * Version:V1.0
 */
public class MyDSAnnotationAdvisor extends AbstractPointcutAdvisor {

    private Advice advice;

    private Pointcut pointcut;

    public MyDSAnnotationAdvisor(MyDSMethodInterceptor myDSMethodInterceptor) {
        this.pointcut = buildPointcut();
        this.advice = myDSMethodInterceptor;
    }

    /**
     * 实现DS注解的拦截配置
     * @return
     */
    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return advice;
    }


    private Pointcut buildPointcut() {
        //类级别
        Pointcut cpc = new AnnotationMatchingPointcut(MyDS.class, true);
        //方法级别
        Pointcut mpc = AnnotationMatchingPointcut.forMethodAnnotation(MyDS.class);
        return new ComposablePointcut(cpc).union(mpc);
    }

}
