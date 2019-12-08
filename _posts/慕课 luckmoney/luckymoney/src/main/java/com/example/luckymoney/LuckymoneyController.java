package com.example.luckymoney;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class LuckymoneyController {

    @Autowired
    private LuckymoneyRepository repository;

    @Autowired
    private LuckymoneyService luckymoneyService;

    /*
    获取红包列表
     */
    @GetMapping("/luckymoneys")
    public List<Luckymoney> list(){
        return repository.findAll();
    }

    /*
    创建红包(发红包)，参数为发红包的人，红包的金额
     */
    @PostMapping("/luckymoneys")
    public Luckymoney create(@RequestParam("producer") String producer, @RequestParam("money") BigDecimal money){
        Luckymoney luckymoney=new Luckymoney();
        luckymoney.setProducer(producer);
        luckymoney.setMoney(money);
        return repository.save(luckymoney);
    }

    /*
    通过id查询红包
     */
    @GetMapping("/luckymoneys/{id}")
    public Luckymoney findById(@PathVariable("id") Integer id){
        return repository.findById(id).orElse(null);
    }

    /*
    更新红包（领红包），id值，领红包的人
    获取参数的内容：@PathVariable("")
     */
    @PutMapping("/luckymoneys/{id}")
    public Luckymoney update(@PathVariable("id") Integer id,@RequestParam("consumer") String consumer){
        //get():查到luckymoney的内容
        Optional<Luckymoney> optional=repository.findById(id);
        if (optional.isPresent()){
            Luckymoney luckymoney=optional.get();
            luckymoney.setId(id);
            luckymoney.setConsumer(consumer);
            return repository.save(luckymoney);
        }

        /*
        因为只设置了consumer的内容，money和producer都没有设置，所以数据库money和producer的值都为null，
        所以要先查一次数据
         */
//        Luckymoney luckymoney=new Luckymoney();
//        luckymoney.setId(id);
//        luckymoney.setConsumer(consumer);
//        return repository.save(luckymoney);
        return null;
    }

    @PostMapping("/luckymoneys/two")
    public void createTwo(){
        luckymoneyService.createTwo();
    }
}
