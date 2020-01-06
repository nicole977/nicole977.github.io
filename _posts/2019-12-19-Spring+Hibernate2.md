### 使用DetachedCriteria进行分页查询

Spring和Hibernate整合是借助HibernateTemplate进行的，分页查询主要会用到DetachedCriteria进行

数据库的category_表有多条数据数据
![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20191221005442.PNG)

实体类Category
```
public class Category {

	private int id;
    private String name;
	
    getter,setter

    @Override
    public String toString() {
    	return "id:"+id+";name:"+name;
    }
    
}
```

```
public class SpringTest {
  
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        CategoryDao categoryDao = (CategoryDao) applicationContext.getBean("dao");

        DetachedCriteria dc = DetachedCriteria.forClass(Category.class);
        int start =5;   // 从第几条开始查询
        int count =10;  // 每页显示的数量
        List<Category> categorys= categoryDao.findByCriteria(dc,start,count);
        System.out.println(categorys.toString());
    }
}
```

输出：
```
Hibernate: select this_.id as id0_0_, this_.name as name0_0_ from category_ this_ limit ?, ?
[id:12;name:category1, id:13;name:category2, id:14;name:category3, id:15;name:category4, id:16;name:category5, id:17;name:category6, id:18;name:category7, id:19;name:category8, id:20;name:category9, id:21;name:category10]
```

### 使用HibernateTemplate查询总数

通过find方法执行select(*)，接着会返回一个List里面第一个元素，即总数（总数据存在List里的第一个元素里）
```
package com.springhib.test;
  
import java.util.List;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import com.springhib.dao.CategoryDao;
  
public class SpringTest {
  
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        CategoryDao categoryDao = (CategoryDao) applicationContext.getBean("dao");

        List<Long> l =categoryDao.find("select count(*) from Category c");
        long total = l.get(0);  // l.get(0)：就是获取下标为0的元素(集合里的知识)
        System.out.println(total);
    }
}
```

输出：
```
Hibernate: select count(*) as col_0_0_ from category_ category0_
24
```

### 使用HibernateTemplate进行模糊查询

分别使用Hql和Criteria进行模糊查询

```
public class SpringTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"});
        CategoryDao categoryDao = (CategoryDao) applicationContext.getBean("dao");

        List<Category> categorys =categoryDao.find("from Category c where c.name like ?", "%c%");
  
        for (Category category : categorys) {
            System.out.println(category.getName());
        }
  
        DetachedCriteria dc = DetachedCriteria.forClass(Category.class);
        dc.add(Restrictions.like("name", "%6%"));
        categorys =categoryDao.findByCriteria(dc);

        System.out.println("----------------------------------");

        for (Category category : categorys) {
            System.out.println(category.getName());
        }
    }
}
```

输出：
<img src="http://chenchen7.oss-cn-shanghai.aliyuncs.com/20191221004912.PNG" width="700px" />

### 使用c3p0数据库连接池

调整applicationContext.xml以使得其支持c3p0数据库连接池
主要是修改数据源database
1. database class 

从org.springframework.jdbc.datasource.DriverManagerDataSource 改为 com.mchange.v2.c3p0.ComboPooledDataSource

2. driverClassName 改为 driverClass
3. url 改为 jdbcUrl
4. username 改为 user
5. 增加c3p0相关配置

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:aop="http://www.springframework.org/schema/aop"
    xmlns:tx="http://www.springframework.org/schema/tx"
    xmlns:context="http://www.springframework.org/schema/context"
    xsi:schemaLocation="
   http://www.springframework.org/schema/beans
   http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
   http://www.springframework.org/schema/aop
   http://www.springframework.org/schema/aop/spring-aop-3.0.xsd
   http://www.springframework.org/schema/tx
   http://www.springframework.org/schema/tx/spring-tx-3.0.xsd
   http://www.springframework.org/schema/context
   http://www.springframework.org/schema/context/spring-context-3.0.xsd">
  
    <bean name="c" class="com.springhib.pojo.Category">
        <property name="name" value="ccc" />
    </bean>

    <bean name="dao" class="com.springhib.dao.CategoryDao">
        <property name="sessionFactory" ref="sf" />
    </bean>

    <bean name="sf" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
        <property name="dataSource" ref="ds" />
        <property name="mappingResources">
            <list>
                <value>com/springhib/pojo/Category.hbm.xml</value>
            </list>
        </property>
        <property name="hibernateProperties">
            <value>
                hibernate.dialect=org.hibernate.dialect.MySQLDialect
                hibernate.show_sql=true
                hbm2ddl.auto=update
            </value>
        </property>
    </bean>

    <bean name="ds" class="com.mchange.v2.c3p0.ComboPooledDataSource">

        <property name="driverClass" value="com.mysql.jdbc.Driver" />
        <property name="jdbcUrl" value="jdbc:mysql://localhost:3306/htest?characterEncoding=UTF-8" />
        <property name="user" value="root" />
        <property name="password" value="root" />

        <!--连接池中保留的最小连接数。-->
        <property name="minPoolSize" value="10" />

        <!--连接池中保留的最大连接数。Default: 15 -->
        <property name="maxPoolSize" value="100" />

        <!--最大空闲时间,1800秒内未使用则连接被丢弃。若为0则永不丢弃。Default: 0 -->
        <property name="maxIdleTime" value="1800" />

        <!--当连接池中的连接耗尽的时候c3p0一次同时获取的连接数。Default: 3 -->
        <property name="acquireIncrement" value="3" />

        <!--最大的Statements条数 -->
        <property name="maxStatements" value="1000" />

        <!--初始化10条连接 -->
        <property name="initialPoolSize" value="10" />

        <!--定义在从数据库获取新连接失败后重复尝试的次数。Default: 30 -->
        <property name="acquireRetryAttempts" value="30" />

        <!--每隔60秒发一次心跳信号到数据库，以保持连接的活性 -->
        <property name="idleConnectionTestPeriod" value="60" />

    </bean>
</beans>
```























