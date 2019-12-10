### Struts2入门

### 首先，Struts2是做什么的？

Controller层：业务模块流程的控制（创建很多Servlet，jsp，跳转页面等），现在有了Struts2框架，Servlet做的事改用Struts2框架做

Service层：写业务逻辑

Dao层：写sql语句，连接数据库（用Hinernate与mybatis代替）

spring相当于一个容器，贯穿三层

Struts2的核心就是：拦截器，作用是使业务逻辑控制器能够与ServletAPI完全脱离开，Struts2的前身时wehwork()框架基于filter

### 入门demo

建controller层，service层，dao层

建struts.xml

### struts实现方式：



### struts的架构：
页面请求经过核心拦截器(StrutsPrepareAndExcuteFilter[ 在web.xml里 ])访问Action Mapper，获取到Action Mapper对象，再将对象返回给核心拦截器，然后再请求ActionProxy，即将获取到的Action Mapper对象返回给ActionProxy，然后要配置文件管家(ConfigurationManager)进行读取配置文件struts.xml，然后将相关信息返回给ActionProxy，ActionProxy将信息交给ActionInvocation拦截器，其包含很多拦截器(Interceptor1，Interceptor2，Interceptor3等)，执行完拦截器后，执行Action，执行Result，执行jsp页面，然后又执行拦截器(Interceptor3，Interceptor2，Interceptor1)，执行完拦截器后返回给核心拦截器(StrutsPrepareAndExcuteFilter)

拦截器好处：
1. 帮我们封装了很多方法
2. 可插拨式的设计
3. aop的思想

记：
request：前台传给后台
`req.setCharacterEncoding("utf-8");`
response：后台传给前台
`resp.setCharacterEncoding("utf-8");`

package作用：封装Action，package可以有多个Action
name属性：给包取的别名
namespace属性：命名空间，给action定义的访问路径地址
extends：继承一个指定的包，必须继承的（struts-default在struts2-core-2.3.24.jar包中）
abstract：表示此包是抽象的，不能直接用，需要继承，即只能继承不能使用

action：配置action类
name属性：action访问的路径地址
class属性：action类的完整路径地址
method属性：action类的方法名

result：配置结果
name属性：与action类的方法返回值一致
type属性：指定Result类来进行处理的结果集（转发，重定向），默认的是转发

`/index.jsp`标签体：页面相对的路径地址

`<include file=""></include>`就是引用其他的struts配置文件 file="" 地址

struts.xml
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC
	"-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
	"http://struts.apache.org/dtds/struts-2.3.dtd">
	
<struts>
	<package name="HelloAction" namespace="/HelloAction" extends="struts-default">
		<action name="hello" class="com.demo.controller.HelloAction" method="execute">
			<result name="success">/index.jsp</result>
		</action>
	</package>

    <include file=""></include>
</struts>
```

### 常量配置

