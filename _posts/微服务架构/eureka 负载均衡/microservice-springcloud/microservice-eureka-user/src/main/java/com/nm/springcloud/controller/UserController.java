package com.nm.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {
    @Autowired
    private RestTemplate restTemplate;

    //查询与用户相关的订单
    @GetMapping("/findOrdersByUser/{id}")
    public String findOrdersByUser(@PathVariable String id){
        //假设用户只有一个订单，并且订单id为123
        int oid=123;
        System.out.println(oid);
//        return this.restTemplate.getForObject("http://localhost:7900/order/"+oid,String.class);

        //使用提供方的实例名称来执行已注册服务列表中的实例方法
        return this.restTemplate.getForObject("http://microservice-eureka-order1/order/"+oid,String.class);
    }

}
