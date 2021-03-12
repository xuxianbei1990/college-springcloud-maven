package college.processs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class ProcesssApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcesssApplication.class, args);
    }

}
