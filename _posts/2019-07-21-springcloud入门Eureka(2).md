---
title: springcloud入门Eureka2
date: 2019-07-21 01:07:32
tags:
  - 微服务架构
---

### 使用Eureka注册服务

.......

嘤嘤嘤一把辛酸泪好吧〒▽〒，报了一堆错误呐，明明教程写得很简单，先写错误吧

1. 报错Protocol handler start failed：端口有冲突，把端口改掉

2. 报错：NoSuchMethodError: org.springframework.boot.builder.SpringApplicationBuilder，这是因为spring boot和spring cloud版本不匹配导致的，超级难搞，可以参考一下：

    https://blog.csdn.net/fly910905/article/details/79420614

    https://blog.csdn.net/mengmengdastyle/article/details/80065230

    https://blog.csdn.net/qq32933432/article/details/89375630

    直接拿boot的版本去上面那个表里找即可查看当前版本的SpringBoot和哪个版本的Cloud对应

3. 子项目的`eureka.instance.instance-id=http://${spring.cloud.client.ipAddress}:${server.port}/`，要记得加上`http://`，不然还是会报错哒，此句意思是将服务实例设置为IP:端口号的形式

---
关于第2个报错

可参考 https://blog.csdn.net/fly910905/article/details/79420614

在添加第三方依赖的时候，不需要写版本号，Spring IO Platform能够自动帮我们挑选一个最优的版本，保证最大限度的扩展，而且该版本的依赖是经过测试的，可以完美的与其它组件结合使用

Spring IO Platform，简单的可以认为是一个依赖维护平台，该平台将相关依赖汇聚到一起，针对每个依赖，都提供了一个版本号

在Maven中使用Spring IO Platform的方法有两种

1. 将Platform导入到子工程的pom.xml

```
<dependency>
    <groupId>io.spring.platform</groupId>
    <artifactId>platform-bom</artifactId>
    <version>Brussels-SR6</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>
```

2. 继承父工程的Platform

```
<parent>
    <groupId>io.spring.platform</groupId>
    <artifactId>platform-bom</artifactId>
    <version>Brussels-SR6</version>
    <relativePath/>
</parent>
```

这两种方法我都没试过，但我最后还是成功运行了，应该是改版本的时候歪打正着改对了吧╮(╯-╰)╭

---
#### 搭建Spring-Cloud 服务发现Eureka 服务

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720232718.png)

1.搭建Maven父工程microservice-springcloud，在pom.xml添加版本依赖
```
  <dependencies>

    <!--依赖Eureka启动器-->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-starter-eureka-server</artifactId>
      <version>1.4.7.RELEASE</version>
    </dependency>

    <!--spring cloud的版本信息-->
    <dependency>
      <groupId>org.springframework.cloud</groupId>
      <artifactId>spring-cloud-dependencies</artifactId>
      <version>Greenwich.RELEASE</version>
      <type>pom</type>
    </dependency>

  </dependencies>

  <plugins>
    <!--spring boot的编译插件:项目需要编译-->
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
    </plugin>
  </plugins>
```

2.搭建子工程

①. 搭建注册中心microservice-eureka-server，在pom.xml添加版本依赖

```
<!--eureka-server的启动器-->
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka-server</artifactId>
            <version>1.4.7.RELEASE</version>
        </dependency>
    </dependencies>
```

application.yml

由于本demo是一个注册中心，是不需要向自己注册和检索服务的，所以register-with-eureka和fetch-registry都设置为false

```
server:
    port: 8761
eureka:
    instance:   # 管理实例，例：发布一个项目OA，访问http://ip:port/oa
        hostname: localhost
    client:
        register-with-eureka: false
        fetch-registry: false
        service-url:
            defaultZone: http://${eureka.instance.hostname}:${server.post}/eureka/      # 注册中心的地址
        server:
            enable-self-preservation: false
```

Eureka注册中心配置文件application.yml
* server-port：服务注册中心的端口，端口任意
* spring-application-name：项目注册到Eureka显示的调用名称，类似于域名
* eureka.client.register-with-erueka：是否将自己注册到Eureka，默认为true
* eureka.client.fetch-registry：是否向Eureka获取注册信息，默认为true
* spring.jmx.default-domain：区分spring-boot项目

程序入口EurekaApplication
```
package com.nm.eurekademo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer //表明它是一个Eureka服务(注册中心)
@SpringBootApplication
public class EurekaApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaApplication.class, args);
    }
}
```

②. 搭建客户端工程（消费方）microservice-eureka-user，在pom.xml添加版本依赖
```
<dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>

        <!--devtools：热部署，更新完的程序自动更新-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-parent</artifactId>
            </plugin>
        </plugins>
    </build>
```

application.yml
```
server:
    port: 8081  # 指定该Eureka实例的端口号
eureka:
    instance:
        prefer-id-address: false    # 是否显示主机的IP
        instance-id: http://${spring.cloud.client.ipAddress}:${server.port}/    # 将服务实例设置为IP:端口号的形式
    client:
        service-url:
            defaultZone: http://localhost:8761/eureka/      # 指定Eureka服务端地址，相当于找中介(指定注册中心)
spring:
    application:
        name: microservice-eureka-user  # 指定应用名称
```

程序入口Application
```
package com.nm.controller;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
@SpringBootApplication
@EnableEurekaClient
@RestController
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

@EnableEurekaClient:用于声明标注类是一个Eureka客户端组件

父工程pom.xml

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720233614.png)

子工程（两个）pom.xml

注册中心

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720233624.png)

客户端

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720233637.png)

测试：先运行注册中心，在地址栏输入`http://localhost:8761/`

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720233521.png)

再运行Eureka客户端，刷新地址

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720233528.png)

成功撒花~~~
