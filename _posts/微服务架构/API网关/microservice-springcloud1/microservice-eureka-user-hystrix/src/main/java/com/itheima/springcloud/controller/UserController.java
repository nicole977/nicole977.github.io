package com.itheima.springcloud.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 查找与用户相关的订单
     * 使用@HystrixCommand注解指定当该方法发生异常时调用的方法
     * 回调方法的参数类型以及返回值必须要和原方法保持一致
     */
    @GetMapping("/findOrderByUser/{id}")    //指定请求的url方法
    @HystrixCommand(fallbackMethod = "fallbackInfo")
    public String findOrderByUser(@PathVariable Integer id){
        //假设用户只有一个订单，并且订单id为123
        int oid=123;
        return this.restTemplate.getForObject("http://microservice-eureka-order/order/"+oid,String.class);
    }

    //返回信息方法
    public String fallbackInfo(@PathVariable Integer id){
        return "服务不可用，请稍后再试";
    }
}
