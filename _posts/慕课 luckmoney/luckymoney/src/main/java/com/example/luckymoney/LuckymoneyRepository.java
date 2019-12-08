package com.example.luckymoney;

import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository是访问数据库的关键接口；第一个参数是数据库的实体类，第二个参数是id的类型
public interface LuckymoneyRepository extends JpaRepository<Luckymoney,Integer> {


}
