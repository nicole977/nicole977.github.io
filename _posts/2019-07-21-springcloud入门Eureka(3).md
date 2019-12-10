---
title: springcloud入门Eureka3
date: 2019-07-21 01:07:32
tags:
  - 微服务架构
---

注册中心已完成，服务消费方已搭建好，接下来写服务提供方

创建一个子项目，服务提供方，命名为microservice-eureka-order1

在pom.xml添加依赖
```
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
    </dependencies>
```

写配置文件
```
server:
  port: 7900  # 指定该Eureka实例的端口号
eureka:
  instance:
    prefer-id-address: true    # 是否显示主机的IP
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/      # 指定Eureka服务端地址，相当于找中介(指定注册中心)
spring:
  application:
    name: microservice-eureka-order1  # 指定应用名称
```

实体类订单类Order
```
package com.nm.springcloud.po;

public class Order {
    private String id;
    private Double price;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;

    getter,setter,toString
}
```

服务信息工具类（监听服务实例端口），此工具类只是为了查看端口信息
```
package com.nm.springcloud.util;

import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration  //注册组件
public class ServiceInfoUtil implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {

    //声明event对象，该对象用于获取运行服务器的本地端口
    private static EmbeddedServletContainerInitializedEvent event;

    @Override
    public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
        ServiceInfoUtil.event=event;
    }

    //获取端口号
    public static int getPort() {
        int port=event.getEmbeddedServletContainer().getPort();
        return port;
    }
}
```

控制器类OrderController，创建一条订单，通过id查找，id任意
```
package com.nm.springcloud.controller;

import com.nm.springcloud.po.Order;
import com.nm.springcloud.util.ServiceInfoUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
    //通过id查询订单
    @GetMapping("/order/{id}")
    public String findOrderById(@PathVariable String id){
        
        //打印端口信息
        System.out.println("------------------"+ServiceInfoUtil.getPort());

        Order order=new Order();
        order.setId("123");
        order.setPrice(23.5);
        order.setReceiverAddress("北京市昌平区");
        order.setReceiverName("小韩");
        order.setReceiverPhone("123456789945");

        return order.toString();
    }
}
```

程序入口
```
package com.nm.springcloud;

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
```

完善服务消费方microservice-eureka-user

创建控制器类UserController
```
package com.nm.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {
    @Autowired
    private RestTemplate restTemplate;

    //查询与用户相关的订单
    @GetMapping("/findOrdersByUser/{id}")
    public String findOrdersByUser(@PathVariable String id){
        //假设用户只有一个订单，并且订单id为123
        int oid=123;
        System.out.println("http://localhost:7900/order/"+oid);
        return this.restTemplate.getForObject("http://localhost:7900/order/"+oid,String.class);
//        return this.restTemplate.getForObject("http://microservice-eureka-order1/order"+oid,String.class);
    }
}
```

程序入口Application
```
package com.nm.springcloud;
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

    /**
     * 客户端调用服务端
     *
     * 实例化RestTemplate
     * RestTemplate是Spring提供的用于访问Rest服务的客户端，它提供了多种便捷访问远程Http服务的方法，能够大大提高客户端的编写效率。
     */
    @Bean
//    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

测试：先运行注册中心，在地址栏输入`http://localhost:8761/`

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235001.png)

运行提供方`http://localhost:7900/order/2`，id随意，浏览器页面输出

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235023.png)

运行消费方`http://localhost:8081/findOrdersByUser/2`，id随意，浏览器页面输出

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235033.png)

刷新注册中心

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235012.png)

报错：

1.`java.lang.IllegalArgumentException: Invalid character found in method name. HTTP method names must be tokens`：
    这是由于请求的协议不对，把https换成http就可以了

2.order子项目报错`java.lang.NullPointerException: null`：
    漏写工具类的注册组件@Configuration注解

3.user子项目报错`java.lang.IllegalStateException: No instances available for localhost`：
    在microservice-eureka-user子项目的RestTemplate类方法前加了@LoadBalanced，加了ribbon的注解@LoadBalanced就不能直接访问地址了，因为在配置提供方的application.xml时，配置了spring.application.name的参数值
```
spring:
  application:
    name: microservice-eureka-order1  # 指定应用名称
```

所以不能直接访问地址，要将地址改为

```
return this.restTemplate.getForObject("http://microservice-eureka-order1/order/"+oid,String.class);
```

网上说，

提供方必须要指定 spring.application.name: xxxxxx

消费方要使用提供方的spring.application.name指定的值

如： return restTemplate.getForObject("http://xxxxxx/", String.class);

---

插曲：

创建服务提供方的时候yml文件的图标一直都没有变成绿色，也没有自动补全，虽然运行的时候可以识别到yml文件，但不会自动补全就觉得很麻烦，而且跟其他子项目不太一样

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235203.png)

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235213.png)

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235227.png)

百度了一下，说可能是没有下载YAML插件，但我下载了也没反应，后来查到是说因为插件只是辅助功能，前提是项目也就是`Module中配置Spring相关管控,告知IDea这个`。不然任何版本都不会提示，然后又去查怎么配置Spring相关管控，查了半天还是不会弄鸭QAQ，本来想放弃来着，后来又翻到一篇博客说

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235118.png)

恍惚想起创建提供方时我那个地方是没改的

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235131.png)

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235143.png)

重新创建以后就可以了

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190720235152.png)