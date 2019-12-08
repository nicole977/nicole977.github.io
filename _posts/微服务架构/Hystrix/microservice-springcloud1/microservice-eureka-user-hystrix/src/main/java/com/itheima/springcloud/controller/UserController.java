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

    @GetMapping("/findOrderByUser/{id}")
    @HystrixCommand(fallbackMethod = "fallbackInfo")
    public String findOrderByUser(@PathVariable Integer id){
        int oid=123;
        return this.restTemplate.getForObject("http://microservice-eureka-order/order/"+oid,String.class);
    }

    public String fallbackInfo(@PathVariable Integer id){
        return "服务不可用，请稍后再试";
    }
}
