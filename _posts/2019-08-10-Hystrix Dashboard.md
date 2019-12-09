---
title: Hystrix Dashboard
categories: 微服务架构
date: 2019-08-10 01:07:45
tags:
  - 微服务架构
---

### Hystrix Dashboard的使用

Hystrix除了对不可用的服务进行断路隔离外，还能够对服务进行实时监控。

想要实时的对服务进行监控，须在项目中添加相关的监控依赖

microservice-eureka-user-hystrix子工程

pom.xml加上
```
<!--hystrix的服务监控的组件-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuactor</artifactId>
</dependency>
```

分别启动注册中心(8761)，服务提供者(8082)和服务消费者(7900)工程；记住一定要运行一下`http://localhost:8082/findOrderByUser/1`,否则将由于系统接口都未被调用，而只输出ping；

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000253.PNG)

运行`http://localhost:8082/findOrderByUser/1`后,访问`http://localhost:8082/hystrix.stream`，可看到：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000307.PNG)

Hystrix Dashboard是作为断路器状态的一个组件，提供了数据监控和友好的图形化界面，长这样：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000331.PNG)

访问`http://localhost:8083/hystrix.stream`，输入需要监控的url

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000805.PNG)

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000356.PNG)

新建microservice-hystrix-dashboard子工程

pom.xml
```
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-hystrix-dashboard</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

application.yml
```
server:
  port: 8083
spring:
  application:
    name: microservice-hystrix-dashboard  # 指定应用名称
```

Application.java
```
package com.itheima.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard     //开启Hystrix Dashboard
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
```






