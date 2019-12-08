package springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient     //服务提供方
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }

}