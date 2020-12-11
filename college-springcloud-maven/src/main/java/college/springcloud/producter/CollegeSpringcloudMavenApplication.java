package college.springcloud.producter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@MapperScan("college.springcloud.**.mapper.**")
@SpringBootApplication
/**
 *
 *  @EnableAspectJAutoProxy(exposeProxy = true)
 *  配合
 * <dependency>
 * 			<groupId>org.aspectj</groupId>
 * 			<artifactId>aspectjweaver</artifactId>
 * 			<version>1.9.5</version>
 * 		</dependency>
 *
 * 		<!-- https://mvnrepository.com/artifact/org.aspectj/aspectjrt -->
 * 		<dependency>
 * 			<groupId>org.aspectj</groupId>
 * 			<artifactId>aspectjrt</artifactId>
 * 			<version>1.9.5</version>
 * 		</dependency>
 */
@EnableAspectJAutoProxy(exposeProxy = true)
public class CollegeSpringcloudMavenApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollegeSpringcloudMavenApplication.class, args);
    }

}
