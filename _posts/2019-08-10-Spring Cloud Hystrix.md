---
title: Spring Cloud Hystrix：服务容错的保护机制
date: 2019-08-10 01:07:45
tags:
  - 微服务架构
---

### Spring Cloud Hystrix：服务容错的保护机制

Hystrix在使用时的3中状态：关闭，半打开，打开

Spring Cloud Hystrix能保证服务调用者在调用异常服务时快速的返回结果，避免大量的同步等待，这是通过HystrixCommand的fallback()方法实现的

在应用中使用Spring Cloud Hystrix来实现断路器的容错功能，并使用FallBack()方法为熔断或异常提供备选方案

测试：

microservice-springcloud1工程

创建子项目microservice-eureka-user-hystrix，运行，由于没有运行order子项目，没有服务提供方，所以hystrix子项目不会报错，会显示友好的提示界面

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000117.png)

在pom.xml添加依赖
```
<parent>
    <artifactId>microservice-springcloud1</artifactId>
    <groupId>com.nm</groupId>
    <version>1.0-SNAPSHOT</version>
</parent>
<modelVersion>4.0.0</modelVersion>

<artifactId>microservice-eureka-user-hystrix</artifactId>
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <!--hystrix依赖-->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-hystrix</artifactId>
        <version>1.3.6.RELEASE</version>
    </dependency>
</dependencies>
```

application.yml
```
server:
  port: 8082    # 指定该Eureka实例的端口号
eureka:
  instance:
    prefer-ip-address: true    # 是否显示主机的IP
    instance-id: http://${spring.cloud.client.ipAddress}:${server.port}/
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/      # 指定Eureka服务端地址，相当于找中介(指定注册中心)
spring:
  application:
    name: microservice-eureka-user-hystrix  # 指定应用名称
```

用户控制器类UserController
```
package com.itheima.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查找与用户相关的订单
     * 使用@HystrixCommand注解指定当该方法发生异常时调用的方法
     * 回调方法的参数类型以及返回值必须要和原方法保持一致
     */
    @GetMapping("/findOrderByUser/{id}")    //指定请求的url方法
    @HystrixCommand(fallbackMethod = "fallbackInfo")
    public String findOrderByUser(@PathVariable Integer id){
        //假设用户只有一个订单，并且订单id为123
        int oid=123;
        return this.restTemplate.getForObject("http://microservice-eureka-order/order/"+oid,String.class);
    }

    //返回信息方法
    public String fallbackInfo(@PathVariable Integer id){
        return "服务不可用，请稍后再试";
    }
}
```

程序入口
```
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
```

当输入地址`http://localhost:8082/findOrderByUser/1`，hystrix子项目不会报错，会显示友好的提示界面

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000130.png)

---

二更：

隔太久补充，现在全乱了，记得之前( microservice-springcloud工程 )是只能调用到fallbackInfo()方法，运行了order子项目网页界面还是显示"服务不可用，请稍后再试"，现在再过一遍代码( microservice-springcloud1工程 )发现是@HystrixCommand注解不起作用，无法调用到fallbackInfo()方法(￣_￣|||)

<<<<<<< HEAD
要注意的地方有：
=======
隔太久补充，现在全乱了，先记下来再明天补全吧，记得之前是只能调用到fallbackInfo()方法，运行了order子项目网页界面还是显示"服务不可用，请稍后再试"，现在再过一遍代码发现是@HystrixCommand注解不起作用，没有调用到fallbackInfo()方法(￣_￣|||)
>>>>>>> 67cde45c4d8e5f9adbdf10ed76af1e434a646164

如果pom.xml导入了下面这两个包，microservice-eureka-order是没法向注册中心注册服务的
```
spring-cloud-netflix-eureka-client
spring-boot-starter-web
```

主入口Application.java一定要记得加上@EnableEurekaClient注解，否则order子项目也不能向注册中心注册服务

---

三更:

无法调用到fallbackInfo()方法问题：是因为没有开启断路器，在主方法Application.java添加@EnableCircuitBreaker注解即可←_←

