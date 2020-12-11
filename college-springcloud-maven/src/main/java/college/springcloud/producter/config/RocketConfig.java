package college.springcloud.producter.config;

import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author: xuxianbei
 * Date: 2020/11/16
 * Time: 16:59
 * Version:V1.0
 */
@Component
public class RocketConfig implements ApplicationListener<ContextRefreshedEvent> {


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getSource() instanceof DefaultRocketMQListenerContainer) {
            System.out.println("=================");
        }
    }
}
