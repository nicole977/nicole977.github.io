package com.example.luckymoney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * @RestController=@Controller+@ResponseBody：Spring4后的新注解，原来返回json需要@ResponseBody配合@Controller
 * @Controller：处理http请求
 * @RequestMapping：配置url映射
 *
 * @PathVariable：获取url中的数据
 * @RequestParam：获取请求参数的值
 */
@RestController
public class TestController {

//    @Value("${minMoney}")
//    private BigDecimal minMoney;    //金额的类型设置为BigDecimal，不用int，double
//
//    @Value("${description}")
//    private String description; //说明

    @Autowired
    private LimitConfig limitConfig;

//    @GetMapping("/study/{id}")
//    public String say(@PathVariable("id") Integer id){
//        return "说明："+limitConfig.getDescription()+";id："+id;
////        return "index";
//    }

//    @GetMapping("/study")
//    public String say(@RequestParam("id") Integer myid){
//        return "说明："+limitConfig.getDescription()+";id："+myid;
//    }

//    @GetMapping("/study")
    @PostMapping("/study")
    public String say(@RequestParam(value="id",required = false,defaultValue = "0") Integer myid){
        return "说明："+limitConfig.getDescription()+";id："+myid;
    }
}
