---
title: springboot与Redis的集成2
date: 2019-07-22 01:07:22
tags:
  - 微服务架构
---

做一个表单，从数据库读取数据显示在浏览器上并缓存到redis中，当再次刷新浏览器时，不会去数据库读取数据，而是在redis里读取

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000549.png)

实体类User
```
package com.example.demo.po;

import java.io.Serializable;

//一定要实现序列化接口用于序列化
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer t_id;
    private String loginname;
    private String username;
    private String address;
    private Integer sal;
    private Integer comm;

    getter,setter
}
```

UserMapper接口，对数据库进行持久化操作
```
package com.example.demo.mapper;

import com.example.demo.po.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {
    @Select("select * from t_user")
    public List<User> queryUsers();

    @Delete("delete * from t_user where t_id=#{t_id}")
    public void deleteUserById(Integer t_id);
}
```

UserService，负责业务逻辑，先设计接口，再设计实现该接口的类
```
package com.example.demo.service;

import com.example.demo.po.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {

    public List<User> queryUsers();

    public void deleteUserById(Integer t_id);
}
```

UserServiceImpl
```
package com.example.demo.service.impl;

import com.example.demo.mapper.UserMapper;
import com.example.demo.po.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    //注入Mapper
    @Autowired
    private UserMapper userMapper;

    /*
    @Cacheable:缓存注解
    value：缓存的名称，必须指定至少一个
    key：可不写，不写会自动生成
     */
    @Cacheable(value = "UserCache",key = "'user.queryUsers'")    //支持缓存
    @Override
    public List<User> queryUsers() {
        List<User> list=userMapper.queryUsers();
        return list;
    }

    @CacheEvict(value = "UserCache",key = "'user.queryUsers'")  //清除缓存
    @Override
    public void deleteUserById(Integer t_id) {
        System.out.println("删除了"+t_id+"号客户");
        userMapper.deleteUserById(t_id);
    }
}
```

UserController控制器，负责控制业务逻辑
```
package com.example.demo.controller;

import com.example.demo.po.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //查询
    @RequestMapping(value = "/selectUser")
    private List<User> selectUsers() {
        System.out.println("登录");
        return this.userService.queryUsers();
    }

    //删除
    @RequestMapping("/deleteUser/{t_id}")
    private void deleteUsers(@PathVariable Integer t_id) {
        userService.deleteUserById(t_id);
    }
}
```

java配置类RedisConfig.java
```
package com.example.demo.configurer;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.Serializable;

@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory){
        // Spring封装了RedisTemplate对象来进行对Redis的各种操作，它支持所有的Redis原生的api
        RedisTemplate<String, Serializable> template=new RedisTemplate<>();
        // key值的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        // value值的序列化采用GenericJackson2JsonRedisSerializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}

```

程序入口DemoApplication
```
package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
//开启缓存
@EnableCaching
@MapperScan("com.example.demo.mapper")
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
```

配置文件application.yml
```
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/oa?serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.jdbc.Driver
        username: root
        password: root

    redis:
        host: localhost
        port: 6379
        password:
        timeout: 1000
        database: 1

logging:
    level:
        com.example.demo: debug
```

pom.xml
```
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!--MyBatis启动器-->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>

        <!--MySQL驱动-->
      <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- 引入redis依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.1.4.RELEASE</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-redis</artifactId>
            <version>2.1.8.RELEASE</version>
        </dependency>

    </dependencies>
```

注意点：
1. 实体类记得实现序列化接口
2. @Cacheable缓存注解的格式是@Cacheable(value = "?",key = "'?'")，key可以不写，不写会自动生成，如果要写key记得双引号里还有一对单引号
3. 配置文件application.yml中database设置成1，是在第二个库，而cmd默认查找的是0，所以要记得先select 1，再进行操作
4. 记得开启缓存@EnableCaching
5. 总的来说就是先写好依赖，再在配置文件写配置，在程序入口开启缓存，在对应的方法写对应的注解