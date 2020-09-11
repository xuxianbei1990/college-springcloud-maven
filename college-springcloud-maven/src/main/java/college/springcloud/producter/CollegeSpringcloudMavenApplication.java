package college.springcloud.producter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("college.springcloud.**.mapper.**")
@SpringBootApplication
public class CollegeSpringcloudMavenApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollegeSpringcloudMavenApplication.class, args);
	}

}
