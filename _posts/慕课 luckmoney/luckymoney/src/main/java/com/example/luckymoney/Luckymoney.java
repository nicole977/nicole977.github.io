package com.example.luckymoney;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class Luckymoney {

    @Id
    @GeneratedValue     //自增
    private Integer id;
    private BigDecimal money;
    //发送方
    private String producer;
    //接收方
    private String consumer;

    public Luckymoney(){

    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }
}
