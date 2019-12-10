---
title: springcloud入门Eureka1
date: 2019-07-21 01:07:21
tags:
  - 微服务架构
---

spring cloud是一套完整的微服务解决方案，是在spring boot的基础上构建的

spring cloud常用的操作有5个：
1. 服务发现——Netflix Eureka
2. 客服端负载均衡——Netflix Ribbon
3. 断路器——Netflix Hystrix
4. 服务网关——Netflix Zuul
5. 分布式配置——Spring Cloud Config

### Eureka介绍

1.在spring cloud的子项目中，spring cloud Netflix提供了Eureka来实现服务的发现功能。

2.`Eureka`是Netflix开发的一个服务发现框架，本身基于REST的服务，主要用于定位运行在AWS域中的中间层服务，以达到`负载均衡`和`中间层服务故障转移的目的`，spring cloud将其集成在自己的子项目spring cloud Netflix中，以实现spring cloud的服务发现功能。

3.Eureka的服务发现包含两大组件：`服务端发现组件`和`客户端发现组件`

服务端发现组件也被称为服务注册中心，主要提供服务的注册功能

客户端发现组件主要用于处理服务的注册与发现

    心跳检查

    客户端发现组件会向注册中心提供自身的服务（房东将房子给中介），并周期性地发送心跳来更新服务（中介将房子a租给租客A，就不能再将房子租给租客B了），默认时间是30s，如果连续3次心跳都不能发现服务，那么Eureka就会将这个服务节点从服务注册表中移除

4.在Eureka的服务发现机制中，包含3个角色：`服务注册中心`，`服务提供者`和`服务消费者`

服务消费者会与注册中心保持心跳连接，一旦服务提供者的地址发生变更时，注册中心会通知服务消费者（一旦中介有了空房，就会联系租客；一旦中介将房子a租给租客A，就不再将房子租给租客B）

Eureka服务提供者和服务消费者之间的角色是可以互换的，因为一个服务既可能是服务消费者，同时也可能是服务提供者（租客也可将租到的房子转租出去，变成二房东）

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721001418.png)

看不懂上图就参考下图

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720232027.png)








