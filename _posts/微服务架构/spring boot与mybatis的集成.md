#### 在spring boot中使用mybatis

以增删改查客户数据为例

1.创建数据表t_user2，有t_id，uname，pass三个字段

2.创建一个spring boot项目

3.在pom.xml添加相关配置

```
    <dependencies>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- springboot和mybatis集成中间件 -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>

        <!-- MySQL工具 -->
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
    </dependencies>

```

4.在application.yml配置好数据库信息

```
spring:
    datasource:
        url: jdbc:mysql://localhost:3306/oa?serverTimezone=Asia/Shanghai
        driver-class-name: com.mysql.jdbc.Driver
        username: root
        password: root
```

5.创建pojo实体类User并写好getter,setter ,toString方法

```
public class User {
    private Integer t_id;
    private String uname;
    private String pass;

    getter,setter,toString
}
```

6.UserDao接口，dao层，对数据库进行数据持久化操作
```
package com.example.demo.dao;

import com.example.demo.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDao {

    //查
    @Select("select * from t_user2 where t_id=#{t_id}")
    public User selectUser(@Param("t_id") int t_id);

    //删
    @Delete("delete from t_user2 where t_id=#{t_id}")
    public void deleteUserById(@Param("t_id")int t_id);

    //增
    @Insert("insert into t_user2(uname,pass) values(#{uname},#{pass})")
    public int insertUserById(@Param("uname")String uname,@Param("pass")String pass);

    //改
    @Update("update t_user2 set uname=#{uname},pass=#{pass} where t_id=#{t_id}")
    public int updateUserById(@Param("t_id")int t_id,@Param("uname")String uname,@Param("pass")String pass);
}
```

7.UserService.java，service层，存放业务逻辑处理，先创建接口，然后再创建实现该接口的类
```
package com.example.demo.service;

import com.example.demo.pojo.User;

public interface UserService {
    public User selectUser(int t_id);

    public void deleteUserById(int t_id);

    public int insertUserById(String uname,String pass);

    public int updateUserById(int t_id,String uname,String pass);
}

```

8.UserServiceImpl.java
```
package com.example.demo.impl;

import com.example.demo.dao.UserDao;
import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User selectUser(int t_id) {
        return userDao.selectUser(t_id);
    }

    @Override
    public void deleteUserById(int t_id) {
        userDao.deleteUserById(t_id);
    }

    @Override
    public int insertUserById(String uname, String pass) {
        return userDao.insertUserById(uname,pass);
    }

    @Override
    public int updateUserById(int t_id, String uname, String pass) {
        return userDao.updateUserById(t_id,uname,pass);
    }
}
```

9.UserController.java，控制器类，调用Service层的接口来控制业务逻辑
```
package com.example.demo.controller;

import com.example.demo.pojo.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

//spring boot与mybatis的集成
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("select")
    public User selectUser(int t_id) {
        return userService.selectUser(t_id);
    }

    @RequestMapping("delete")
    public void deleteUserById(int t_id) {
        System.out.println("删除了"+t_id+"的数据");
        userService.deleteUserById(t_id);
    }

    @RequestMapping("insert")
    public int insertUserById(String uname, String pass) {
        System.out.println("添加了一条数据："+uname+";"+pass);
        return userService.insertUserById(uname,pass);
    }

    @RequestMapping("update")
    public int updateUserById(int t_id, String uname, String pass) {
        System.out.println("修改了t_id为"+t_id+"的数据"+uname+";"+pass);
        return userService.updateUserById(t_id,uname,pass);
    }
}

```

10.启动主程序DemoApplication

11.打开浏览器访问`http://localhost:8080/select?t_id=7`,浏览器显示出t_id=7的客户数据；

访问`http://localhost:8080/delete?t_id=7`,删除t_id=7的客户数据；

访问`http://localhost:8080/insert?uname=aaa&pass=bbb`,添加tname=aaa且pass=bbb的客户数据；

访问`http://localhost:8080/update?t_id=7&uname=ccc&pass=ddd`,修改t_id=7的tname=ccc且pass=ddd

---

一个很无聊的错误

刚刚进行添加和修改数据的方法时，刷新数据库时发现添加到数据库的数据为null，修改也是

然后看到控制台输出这样一条信息：
```
2019-05-27 06:43:02.066  INFO 18528 --- [nio-8080-exec-2] com.zaxxer.hikari.pool.PoolBase          : HikariPool-1 - Driver does not support get/set network timeout for connections. (com.mysql.jdbc.JDBC4Connection.getNetworkTimeout()I)
```
查了一下发现这只是一个警告，网络超时未实现的警告

最后发现是因为地址栏的uname写错了，我写成了tname，但很奇怪明明字段名是错的为什么还能添加修改成功？

。。。。。。

我貌似从头到尾都没做过判断

。。。。。。。。。。

做了一个简陋的判断后发现还真是，emmmmm我真的超级粗心的。
