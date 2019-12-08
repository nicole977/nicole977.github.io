package com.itheima.springcloud.controller;

import com.itheima.springcloud.po.Order;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    /**
     * 通过id查询订单
     */
    @GetMapping("/order2/{id}")
    public String findOrderById(@PathVariable String id) {
        Order order = new Order();
        order.setId("456");
        order.setPrice(45.6);
        order.setReceiverAddress("北京市昌平区2");
        order.setReceiverName("小韩2");
        order.setReceiverPhone("12222222222");
        return order.toString();
    }
}
