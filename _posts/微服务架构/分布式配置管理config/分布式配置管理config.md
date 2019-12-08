### 分布式配置管理

分布式配置中心组件有很多，如：百度的disconf，阿里的diamand，携程的apollo和spring cloud的config

spring cloud config 主要为分布式系统中的外部配置提供服务器(Config Server)和客户端(Config Client)支持

服务器(Config Server)：分布式配置中心，是一个独立的微服务应用，主要用于管理应用程序各个环境下的配置，默认使用git存储配置文件内容。也可使用SVN存储，或者是本地文件存储

客户端(Config Client)：是Config Server的客户端，即微服务架构中的各个微服务应用。它们通过指定的配置中心(Config Server)来管理应用资源以及与业务相关的配置内容，并在启动时从配置中心获取和加载配置信息

#### 使用本地存储的方式实现配置管理

新建一个配置中心microservice-config-server工程；添加一下依赖：
```
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

在application.yml文件添加配置:
```
spring:
  application:
    name: microservice-config-server
  profiles:
    active: native    #使用本地文件系统的存储方式来保存配置信息
server:
  port: 8888
```

再创建3个分别用于表示开发，预发布和测试的资源配置文件:
application-dev.yml：`clientParam: native-dev-1.0`
application-prod.yml：`clientParam: native-prod-1.0`
application-test.yml：`clientParam: native-test-1.0`

创建启动类，@EnableConfigServer注解用于开启服务端功能：
```
package com.itheima.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer     //开启服务端功能
@SpringBootApplication
public class Application {
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }
}
```

启动工程，运行`http://localhost:8888/microservice-config-server/dev`，成这样：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190724231127.PNG)

新建一个客户端microservice-config-client工程；添加一下依赖：
```
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

在bootstrap.yml文件添加配置:

(注意：这里的配置文件的名字必须为bootstrap.yml或bootstrap.properties，只有这样配置中心才能正常加载，因为bootstrap.yml优先级比application.yml高)

```
spring:
  application:
    name: microservice-config-client
  cloud:
    config:
      profile: prod     # 配置服务中的{profile}
      url: http://localhost:8888/   # 配置中心的地址
server:
  port: 8763
```

创建启动类：
```
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
}
```

启动工程，运行`http://localhost:8763/hello`，成这样：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190724231139.PNG)

启动工程，运行`http://localhost:8763/clientParam`，成这样：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190724231132.PNG)

途中遇到的问题：

当把配置中心的application.yml里的端口号8888改成8762(bootstrap.yml的url的端口号也改成8762)时，报错了：`Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'clientParam' in value "${clientParam}"`，无法访问`http://localhost:8763/hello`和`http://localhost:8763/clientParam`，配置中心microservice-config-server的端口号一定要是8888才不会报错
 
如果不改，就要在客户端microservice-config-client工程的启动类Application.java加上：
```
@Bean
public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
    c.setIgnoreUnresolvablePlaceholders(true);
    return c;
}
```

此时才能访问`http://localhost:8763/hello`和`http://localhost:8763/clientParam`，此时`http://localhost:8763/hello`的浏览页面能正常输出“hello world”，`http://localhost:8763/clientParam`有新的问题，它访问不到预发布环境的配置文件信息，浏览页面成这样：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190724231143.PNG)

原因和解决办法还没找到

#### 使用git存储的方式实现配置管理

application-dev.yml，application-prod.yml，application-test.yml这3个yml文件放到git中

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190810030058.PNG)

修改服务端配置文件application.yml

```
spring:
  application:
    name: microservice-config-server
  cloud:
    config:
      server:
        git:
          uri: https://gitee.com/yunque7/microservice-study-config.git    # 使用git的方式
#          username:
#          password:
server:
#  port: 8762
  port: 8888
```

(若仓库为私有的，则加上username和password属性)

在客户端的配置文件上加上label属性，并将其设置为muster（label属性表示git的分支）

客户端配置文件bootstrap.yml

```
spring:
  application:
    name: microservice-config-client
  cloud:
    config:
      profile: prod   # 配置服务中的{profile}
      url: http://localhost:8888/
      label: master   # 对应git的分支，默认为master
server:
  port: 8763
```

客户端Application.java
```
@RestController
@SpringBootApplication
public class Application {

    @Value("${clientParam}")
    static String clientParam;
//    private String clientParam;

}
```

访问`http://localhost:8888/microservice-config-server/prod`

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190810030057.PNG)

访问`http://localhost:8763/hello`和`http://localhost:8888/clientParam`，此时`http://localhost:8888/hello`的浏览页面能正常输出“hello world”，`http://localhost:8763/clientParam`有问题，又报之前的错`Caused by: java.lang.IllegalArgumentException: Could not resolve placeholder 'clientParam' in value "${clientParam}"`；网上说要将clientParam设置成静态的，运行后不会报错，但浏览页面是空白的

@Value注解赋值

1. @Value("")：直接赋值
2. @Value("#{}")：获取其他bean的属性，或者调用其他bean的方法，或表示常量
3. @Value("${}")：获取对应配置文件中定义的属性值


