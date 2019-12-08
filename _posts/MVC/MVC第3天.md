### JSON数据交互和RESTful支持

当使用JSON存储单个数据（如“abc”）时，一定要使用数组的形式，不要使用Object形式，因为Object形式必须是“名称：值”的形式

#### JSON数据转换

Spring提供了一个`HttpMessageConverter<T>接口`来实现浏览器与控制器类（Controller）之间的数据交互

HttpMessageConverter<T>接口：将请求信息中的数据转换为一个类型为T的对象，并将类型为T的对象绑定到请求方法的参数中，或者将对象转换为响应信息传递给浏览器显示，HttpMessageConverter<T>接口有很多实现类，其中`MappingJackson2HttpMessageConverter`是Spring MVC默认处理JSON格式请求响应的实现类。该实现类`利用Jackson开源包读写JSON数据，将Java对象转换为JSON对象和XML文档`，同时也可以`将JSON对象和XML文档转换为Java对象`

2个重要的JSON格式转换注解，分别为`@RequestBody`和`@ResponseBody`

`@RequestBody`:用于将请求体中的数据绑定到方法的形参中，该注解用在`方法的形参上`，`将json转为Java对象`

`@ResponseBody`:用于直接返回return对象，该注解用在`方法上`，`将Java对象转为json`
`最终都输出json数据`

栗子：

web.xml

```
<!-- 配置前端控制器DispatcherServlet -->
  <servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>
              org.springframework.web.servlet.DispatcherServlet
         </servlet-class>
    <init-param>

    <!-- 初始化时加载配置文件，默认查找/WEB-INF/${servlet-name}-servlet.xml -->
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:springmvc-config.xml</param-value>
    </init-param>

    <!-- 配置服务器启动后立即加载spring mvc配置文件 -->
    <load-on-startup>1</load-on-startup>
  </servlet>

  <servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>

  <!-- 编码过滤器 -->
  <filter>
    <filter-name>CharacterEncodingFilter</filter-name>
    <filter-class>
  		org.springframework.web.filter.CharacterEncodingFilter
  	</filter-class>
    <init-param>
      <param-name>encoding</param-name>
      <param-value>UTF-8</param-value>
    </init-param>
  </filter>
  <filter-mapping>
    <filter-name>CharacterEncodingFilter</filter-name>
    <url-pattern>/*</url-pattern>
  </filter-mapping>
```

在WebContent文件夹里建了一个js文件夹，里面存放jquery-1.11.3.min.js

springmvc-config.xml
```
<!-- 定义组件扫描器，指定需要扫描的包 -->
	<context:component-scan base-package="com.nm.controller" />	
	<!-- 定义视图解析器 -->
	<bean id="viewResolver" class=
    "org.springframework.web.servlet.view.InternalResourceViewResolver">
	     <!-- 设置前缀 -->
	     <property name="prefix" value="/WEB-INF/jsp/" />
	     <!-- 设置后缀 -->
	     <property name="suffix" value=".jsp" />
	</bean>
	
	<!-- 配置注解驱动 -->
	<mvc:annotation-driven/>
	<!-- 配置静态资源的访问映射，此配置中的文件，被springmvc映射访问 -->
	<mvc:resources location="/js/" mapping="/js/**"></mvc:resources>
```

POJO类
```
package com.nm.po;

public class User {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}
}
```

index.jsp
```
<title>json交互</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
	function testJson(){
		//获取输入的用户名和密码
		var username=$("#username").val();
		var password=$("#password").val();
		$.ajax({
			url:"${pageContext.request.contextPath}/testJson",
			type:"post",
			//表发送的数据
			data:JSON.stringify({username:username,password:password}),
			//定义发送请求的数据格式为json字符串
			contentType:"application/json;charset=UTF-8",
			//定义回调相应的数据格式为json字符串，该属性可忽略
			dataType:"json",
			//成功响应的结果
			success:function(data){
				if(data!=null){
					alert("用户名："+data.username+",密码："+data.password);
				}
			}
		})
	}
</script>
</head>
<body>
	<form>
		用户名：<input type="text" name="username" id="username"/>
		密码：<input type="password" name="password" id="password"/>
		<input type="button" value="测试json交互" onclick="testJson()"/>		
	</form>
</body>
```

控制器
```
package com.nm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nm.po.User;

/*
 * @RequestBody:传过来的数据是json类型的数据，因此要将其转成User类型的数据
 * @ResponseBody:返回的数据是User类型的数据，浏览器不认识，因此要将其转成json类型的数据
 */

@Controller
public class UserController {
	@RequestMapping("/testJson")
	@ResponseBody
	public User testJson(@RequestBody User user) {
		//打印接收的json格式数据
		System.out.println(user);
		//返回json格式的响应
		return user;
	}
}
```

在输入框填写用户名和密码，点击按钮有弹框出现，控制台输出"User [username=XXX, password=XXX]"

#### RESTful支持

RESTful概念：就是把请求参数变成请求路径的一种风格

传统的url：`http://.../queryItems?id=1`

RESTful风格的：`http://.../items/1`

栗子：

User类

web.xml

springmvc-config.xml

控制器UserController
```
package com.nm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nm.po.User;

/*
 * @RequestBody:传过来的数据是json类型的数据，因此要将其转成User类型的数据
 * @ResponseBody:返回的数据是User类型的数据，浏览器不认识，因此要将其转成json类型的数据
 */

@Controller
public class UserController {
	//接收RESTful风格的请求，其接收方式为GET
	//@PathVariable("id"):从url中取一个名为id的数据
    @RequestMapping(value="/user/{id}")
	@ResponseBody
	public User selectUser(@PathVariable("id") String id) {
		System.out.println("id="+id);
		User user=new User();
		if(id.equals("1234")) {
			user.setUsername("tom");
		}
		//返回JSON格式的数据
		return user;
	}
}
```

restful.jsp
```
<title>RESTful风格</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
	function search(){
		//获取输入的查询编号
		var id=$('#number').val();
		$.ajax({
			url:"${pageContext.request.contextPath}/user/"+id,
			type:"GET",
			//定义回调响应的数据格式为JSON字符串
			dataType:"json",
			//成功响应的结果
			success:function(data){
				if(data.username!=null){
					alert("您查询的用户是："+data.username);
				}else{
					alert("没找到id为"+id+"的用户");
				}
			}
		})
	}
</script>
</head>
<body>
	<form>
		编号：<input type="text" name="number" id="number"/>
		<input type="button" value="搜索" onclick="search()"/>
	</form>
</body>
```

在地址栏输入`http://localhost:8080/MVCTest14/restful.jsp`，在输入框输入1234和其他的编号，点击搜索按钮会弹出对应的弹框且控制台会打印出id

id=1234

![](https://dbnewyouth.oss-cn-zhangjiakou.aliyuncs.com/images/1556443301146.PNG?Expires=1871802972&OSSAccessKeyId=LTAI91SeAmgnTkb9&Signature=FVaL%2BZxy5VdrKBrOztMXDa%2F1%2Fpk%3D)

id=123456

![](https://dbnewyouth.oss-cn-zhangjiakou.aliyuncs.com/images/1556443205488.PNG?Expires=1871803004&OSSAccessKeyId=LTAI91SeAmgnTkb9&Signature=j53PHiWTWBY1HBM9jUA%2BjG17Q9k%3D)

