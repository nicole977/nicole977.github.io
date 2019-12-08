### 拦截器

用于`拦截用户的请求并作出相应的处理`，例如通过拦截器可以进行`权限验证、记录请求信息的日志、判断用户是否登录`等

通常拦截器类可以通过两种方式来定义:

1.通过实现HandlerInterceptor接口，或继承HandlerInterceptor接口的实现类（如HandlerInterceptorAdapter）来定义

2.通过实现WebRequestInterceptor接口，或继承WebRequestInterceptor接口的实现类来定义

拦截器中的三个方法:

preHandle:控制器方法前执行

postHandle:控制器方法调用之后，且解析视图之前

afterCompletion:整个请求完成执行

拦截器的配置
```
    <mvc:interceptors>

        <!-- 全局拦截器，拦截所有请求 -->
        <bean class="com.itheima.interceptor.CustomInterceptor"/>
       <mvc:interceptor>

            <!-- /**配置，表示拦截所有路径 -->
            <mvc:mapping path="/**"/>

            <!-- 配置不需要拦截的路径 -->
            <mvc:exclude-mapping path=""/>
            <bean class="com.itheima.interceptor.Interceptor1" /> 
       </mvc:interceptor>
       <mvc:interceptor>

            <!-- /hello表示拦截所有以“/hello”结尾的路径 -->
            <mvc:mapping path="/hello"/>
            <bean class="com.itheima.interceptor.Interceptor2" />
        </mvc:interceptor>
        
    </mvc:interceptors>
```

单个拦截器

流程：
![](https://dbnewyouth.oss-cn-zhangjiakou.aliyuncs.com/images/1556443292942.PNG?Expires=1871802711&OSSAccessKeyId=LTAI91SeAmgnTkb9&Signature=ld6xGPsuOzh9RGWVfhQGh47keb8%3D)

例子：

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
	
	<!-- 配置拦截器 -->
	<mvc:interceptors>
		<!-- 使用bean直接定义在<mvc:interceptors>下面的拦截器所有请求 -->
		<bean class="com.nm.intercepter.CustomInterceptor"/>
	</mvc:interceptors>
```

控制器类HelloController.java
```
package com.nm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloController {
	@RequestMapping("/hello")
	public String hello() {
		System.out.println("hello");
		return "success";
	}
}
```

拦截器类CustomInterceptor.java
```
package com.nm.intercepter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

//实现了HandlerInterceptor接口的自定义拦截器类
public class CustomInterceptor implements HandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		System.out.println("preHandle:控制器方法前执行");
		//对拦截到的请求进行放行处理
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
			throws Exception {
		System.out.println("postHandle:控制器方法调用之后，且解析视图之前");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {
		System.out.println("afterCompletion:整个请求完成执行");
	}

}
```

success.jsp
```
<body>
	结果页面ok
</body>
```
在地址栏输入"http://localhost:8080/MVCTest15/hello",控制台打印出
"
preHandle:控制器方法前执行
hello
postHandle:控制器方法调用之后，且解析视图之前
afterCompletion:整个请求完成执行
"

页面显示"结果页面ok"


未完

多个拦截器

栗子：MVCTest16 报错

案例：

![](https://dbnewyouth.oss-cn-zhangjiakou.aliyuncs.com/images/1556443665605.PNG?Expires=1871802890&OSSAccessKeyId=LTAI91SeAmgnTkb9&Signature=xZ380MUwM%2B3m2D0kvHDsm4WvDbI%3D)

web.xml
```
<servlet>
    <servlet-name>springmvc</servlet-name>
    <servlet-class>
    	org.springframework.web.servlet.DispatcherServlet
    </servlet-class>

    <!-- 配置Spring MVC加载配置文件路径 -->
    <init-param>
      <param-name>contextConfigLocation</param-name>
      <param-value>classpath:springmvc-config.xml</param-value>
    </init-param>

    <!-- 配置服务器启动后立即加载Spring MVC配置文件 -->
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>springmvc</servlet-name>
    
    <!--/:拦截所有请求（除了jsp） -->
    <url-pattern>/</url-pattern>
  </servlet-mapping>
```

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
	
	<!-- 配置拦截器 -->
	<mvc:interceptors>
		<mvc:interceptor>
			<mvc:mapping path="/**"/>
			<bean class="com.nm.interceptor.LoginInterceptor"></bean>
		</mvc:interceptor>
	</mvc:interceptors>
```

拦截器类LoginInterceptor.java
```
package com.nm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.nm.po.User;

//登录拦截器类
public class LoginInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object arg2) throws Exception {
		// 获取请求的URL
		String url = request.getRequestURI();
		// URL:除了login.jsp是可以公开访问的，其它的URL都进行拦截
		if (url.indexOf("/login") > 0) {
			return true;
		}

		// 获取Session
		HttpSession session = request.getSession();
		User user=(User) session.getAttribute("USER_SESSION");
		//判断Session中是否有用户数据，如果有，则返回true，继续向下执行
		if(user!=null) {
			return true;
		}
		
		//不符合条件的给出提示信息，并转发到登录页面
		request.setAttribute("msg","您还没登录，请先登录");
		request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
		return false;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object arg2, Exception arg3)
			throws Exception {

	}
}

```

主页面main.jsp
```
<body>
当前用户：${USER_SESSION.username}
<a href="${pageContext.request.contextPath}/logout">退出</a>
</body>
```

登录页面login.jsp
```
<body>
${msg}
<form action="${pageContext.request.contextPath}/login" method="post">
	用户名：<input type="text" name="username"/><br>
	密码：<input type="password" name="password"/><br>
	<input type="submit" value="登录"/>
</form>
</body>
```