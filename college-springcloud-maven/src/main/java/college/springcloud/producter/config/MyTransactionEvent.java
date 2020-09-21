package college.springcloud.producter.config;

import org.springframework.context.ApplicationEvent;

/**
 * @author: xuxianbei
 * Date: 2020/9/21
 * Time: 17:10
 * Version:V1.0
 */
public class MyTransactionEvent extends ApplicationEvent {
    public MyTransactionEvent(String source) {
        super(source);
    }
}
