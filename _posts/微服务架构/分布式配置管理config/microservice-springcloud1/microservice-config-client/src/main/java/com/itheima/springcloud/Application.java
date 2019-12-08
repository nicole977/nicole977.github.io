package com.itheima.springcloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Application {

    @Value("${clientParam}")
    private String clientParam;

    @RequestMapping("/clientParam")
    public String getParam(){
        return this.clientParam;
    }

    @RequestMapping("/hello")
    public String hello(){
        return "hello world";
    }

    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
//        PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
//        c.setIgnoreUnresolvablePlaceholders(true);
//        return c;
//    }
}
