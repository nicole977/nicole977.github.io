---
title: spring boot与ActiveMQ的集成
categories: 微服务架构
date: 2019-07-21 01:07:32
tags:
  - 微服务架构
---

ActiveMQ：消息队列，存放消息的容器

ActiveMQ有2种消息形式：
1. 点对点（queue）
2. 一对多（topic）

为什么会需要消息队列(MQ)呢?查了一堆资料，差不多就是说在高并发的环境下，因为来不及同步处理请求，请求往往会发生阻塞，通过使用消息队列，可以异步处理请求，缓解系统压力

内置的ActiveMQ

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720231918.png)

小栗子：

添加依赖：pom.xml
```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-activemq</artifactId>
            <version>2.1.3.RELEASE</version>
        </dependency>
```

消息生产者，创建send()方法，指定消息发送的目的地及内容
```
package com.example.demo1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;

//队列消息控制器
@RestController
public class QueueController {
    //JmsMessagingTemplate：发送消息到队列Queue中
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;
    @Autowired
    private Queue queue;

    //消息生产者
    @RequestMapping("/send")
    public void send(){
        //指定消息发送的目的地及内容，目的地为Queue对象，所发送的内容为"生产者发送的消息"
        this.jmsMessagingTemplate.convertAndSend(this.queue,"生产者发送的消息");
    }
}
```

消息消费者，监听和读取消息
```
package com.example.demo1.controller;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.web.bind.annotation.RestController;

//客户控制器
@RestController
public class CustomerController {
    /*
    @JmsListener：用于监听JMS消息的注解，该注解的属性destination用于指定要监听的目的地
     */
    //监听和读取消息；destination：目的地
    @JmsListener(destination = "active.queue")
    public void readActiveQueue(String message){
        System.out.println("接收到："+message);
    }
}
```

JMS 百度百科给出的定义：是Java消息服务（Java Message Service）应用程序接口，是一个Java平台中关于面向消息中间件（MOM）的API，用于在两个应用程序之间，或分布式系统中发送消息，进行异步通信。Java消息服务是一个与具体平台无关的API，绝大多数MOM提供商都对JMS提供支持。

简单说就是，两个应用程序之间需要进行通信，我们使用一个JMS服务，进行中间的转发，通过JMS的使用，可解除两个程序之间的耦合。

程序入口：
```
package com.example.demo1;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.Queue;

@SpringBootApplication
public class Demo1Application {

    //创建消息队列对象
    @Bean
    public Queue queue(){
        //消息队列的名称为active.queue
        return new ActiveMQQueue("active.queue");
    }

    public static void main(String[] args) {
        //springboot程序的入口
        SpringApplication.run(Demo1Application.class, args);
    }

}
```

>实际开发中不会用springboot内置的ActiveMQ，大多使用外部的MQ
