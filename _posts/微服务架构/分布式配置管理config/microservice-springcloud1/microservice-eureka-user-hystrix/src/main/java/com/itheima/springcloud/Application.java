package com.itheima.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient    //添加eureka服务
@EnableCircuitBreaker  //开启断路器功能
public class Application {
    /**
     * 实例化RestTemplate
     * RestTemplate是spring提供的用于访问Rest服务的客户端，
     * 它提供了多种便捷访问远程Http服务的方法，能够大大提高客户端的编写效率
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
