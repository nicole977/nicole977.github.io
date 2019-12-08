package com.itheima.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient     //二更：一定要加，注册中心才有
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
