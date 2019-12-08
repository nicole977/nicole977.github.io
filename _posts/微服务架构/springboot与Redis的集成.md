Redis是一个key-value存储，一种NoSql数据库，作为缓存使用，它支持String(字符串)，List(列表)，Set(集合)，Hash(散列)，sorted set(有序集合)

学之前最好先过一遍语法，不然可能会碰到一些很无聊的错误，还要找半天的那种，所以此处先扔一个菜鸟教程：https://www.runoob.com/redis/redis-tutorial.html

RedisConfig.java：Java配置类

RedisC：操作redis的Java类

application.yml：连接redis的配置文件

栗子：

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000520.PNG)

application.yml
```
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/oa?serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.jdbc.Driver
        username: root
        password: root

    redis:
        #redis数据库地址
        host: localhost
        port: 6379
        password:
        timeout: 1000
        #redis数据库索引，默认0
        database: 1
```
>redis默认一共有16个库(0-15号)，spring.redis.database是指redis数据库的索引，默认为0,就是第一个库，在此处我设置成1，也就是第二个库

配置类RedisConfig我还不是很明白
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

//???
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Serializable> redisCacheTemplate(LettuceConnectionFactory redisConnectionFactory){
        //Spring封装了RedisTemplate对象来进行对Redis的各种操作，它支持所有的Redis原生的api
        RedisTemplate<String, Serializable> template=new RedisTemplate<>();
        //key值的序列化采用StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        // value值的序列化采用GenericJackson2JsonRedisSerializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
```

有2种序列化的方式，redisTemplate和StringRedisTemplate

1. Spring封装了RedisTemplate对象来进行对Redis的各种操作，它支持所有的Redis原生的api

2. StringRedisTemplate作为RedisTemplate的子类，只支持KV为String的操作

RedisC.java
```
package com.example.demo.configurer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisC {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //添加
    @GetMapping(value = "/redisAdd")
    public void saveRedis(){
        stringRedisTemplate.opsForValue().set("a","test");
    }

    //获取
    @GetMapping(value = "/redisGet")
    public String getRedis(){
        return stringRedisTemplate.opsForValue().get("a");
    }
}
```

pom.xml
```
<dependencies>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.6</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- 引入redis依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
            <version>2.1.4.RELEASE</version>
        </dependency>

    </dependencies>
```

当我在浏览器输入`http://localhost:8080/redisAdd`时,就是将"a"这个key存入redis中，此时再输入`http://localhost:8080/redisGet`，可看到浏览器页面出现"test"，就是表示我存入的key已经成功了

因为database我设置成1，在第二个库，而cmd默认查找的是0，所以要先select 1，先前不知道，一直在默认的库中查找"a"，找了半天没找到==

![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20190721000529.png)

"a"这个key已存到redis中，第一个是另外一个key，不管
