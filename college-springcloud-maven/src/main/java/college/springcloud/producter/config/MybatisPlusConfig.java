package college.springcloud.producter.config;

import college.springcloud.producter.mybatis.MySqlInjector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: xuxianbei
 * Date: 2020/9/10
 * Time: 15:25
 * Version:V1.0
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }
}
