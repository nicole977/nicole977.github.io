### 整合思路

使DAO继承HibernateTemplate这个类，HibernateTemplate这个类提供了setSessionFactory()方法用于注入SessionFactory，通过spring获取DAO的时候，注入SessionFactory，即将sessioinFactory对象交给Spring容器进行管理

例：

实体类Category
```
package com.springhib.pojo;
  
public class Category {
    private int id;
    private String name;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
```

Category.hbm.xml
```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC
        "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd">

<hibernate-mapping package="com.springhib.pojo">
    <class name="Category" table="category_">
        <id name="id" column="id">
            <generator class="native"></generator>
        </id>
        <property name="name" />
    </class>
</hibernate-mapping>
```

Dao继承HibernateTemplete
```
package com.springhib.dao;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class CategoryDao extends HibernateTemplate{
 
}
```

配置文件applicationContext.xml

创建dao时，会注入sessionfactory，创建sessionFactory时会注入数据源ds
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

    <bean name="dao" class="com.springhib.dao.CategoryDao">
        <property name="sessionFactory" ref="sf" />
    </bean>

    <!-- Spring整合Hibernate -->
    <bean name="sf" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean">
        <property name="dataSource" ref="ds" />

        <!-- 
        在Spring的applicationContext.xml中配置映射文件，通常是在<sessionFactory>这个Bean实例中进行的，若配置的映射文件较少时，可以用sessionFactory的所属类LocalSessionFactoryBean的“mappingResources”属性
         -->
        <property name="mappingResources">
            <list>
                <value>com/springhib/pojo/Category.hbm.xml</value>
            </list>
        </property>

        <!-- 配置Hibernate的可选属性 -->
        <property name="hibernateProperties">
            <value>
                hibernate.dialect=org.hibernate.dialect.MySQLDialect
                hibernate.show_sql=true
                hbm2ddl.auto=update
            </value>
        </property>
    </bean>

    <!-- 配置Hibernate的JDBC属性 -->
    <bean name="ds" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver" />
        <property name="url" value="jdbc:mysql://localhost:3306/htest?characterEncoding=UTF-8" />
        <property name="username" value="root" />
        <property name="password" value="root" />
    </bean>  
 
</beans>
```

测试类
```
public class SpringTest {
  
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
        CategoryDao dao = (CategoryDao) context.getBean("dao");
        List<Category> cs= dao.find("from Category c");
        System.out.println(cs);
    }
}
```

输出：
![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20191220001452.PNG)

因为CategoryDao类继承了HibernateTemplate,所以可以直接使用
1. save 增加
2. get 获取
3. update 修改
4. delete 删除

修改测试类
```
public class SpringTest {
 
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
        CategoryDao categoryDao = (CategoryDao) applicationContext.getBean("dao");
        Category category = new Category();
        category.setName("category category777");
        System.out.println("添加一条数据--------c.getName()："+category.getName());
        //添加
        categoryDao.save(category);
        
        //获取
        Category category6 = categoryDao.get(Category.class, 6);
        System.out.println("获取一条数据--------c6："+category6.getId()+";"+category6.getName());
        
        Category category5 = categoryDao.get(Category.class, 5);
        category5.setName("category_zzz");
        //修改
        categoryDao.update(category5);
        System.out.println("修改一条数据--------category5："+category5.getId()+";"+category5.getName());
    }
}
```

输出：
![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20191220010918.PNG)

删除操作
```
public class SpringTest {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(new String[] { "applicationContext.xml" });
        CategoryDao categoryDao = (CategoryDao) applicationContext.getBean("dao");
        
        //删除
        Category category3 = categoryDao.get(Category.class, 3);
        categoryDao.delete(category3);
        System.out.println("删除一条序号为3的数据");

    }
}
```

当数据库的category_表的id设置成自动增长时，还对其做删除操作，会造成主键ID重复，我的Category.hbm.xml的
```
<id name="id" column="id">
    <generator class="native"></generator>
</id>
```
generator的class值不是等于increment,但数据库的category_表的id我设置成自动增长，所以报错，改了就可以了
```
Caused by: org.hibernate.exception.ConstraintViolationException: Could not execute JDBC batch update
```

输出：
![](http://chenchen7.oss-cn-shanghai.aliyuncs.com/20191220011058.PNG)

但由于id不能设置成自动增长，但id又是主键，所以进行添加操作时会报错==





