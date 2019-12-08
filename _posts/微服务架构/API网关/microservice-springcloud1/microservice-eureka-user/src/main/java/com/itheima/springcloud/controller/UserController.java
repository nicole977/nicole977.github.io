package com.itheima.springcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/findOrdersByUser/{id}")
    public String findOrdersByUser(@PathVariable Integer id){
        int oid=123;
        System.out.println("http://localhost:7900/order/"+oid);
//        return this.restTemplate.getForObject("http://localhost:7900/order/"+oid,String.class);
        return this.restTemplate.getForObject("http://microservice-eureka-order/order/"+oid,String.class);
    }
}
