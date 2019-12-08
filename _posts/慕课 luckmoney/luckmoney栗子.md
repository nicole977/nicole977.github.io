#### 通过spring boot实现发红包的小例子

1.pom.xml文件

配置文件有3个，一个是测试时用，一个是真正投入生产环境时用，一个是用来调用这两个环境
```
application.yml
application-dev.yml
application-pro.yml
```

2.主入口

创建实体类Luckymoney，属性有
```
    @Id
    @GeneratedValue     //设置id自增
    private Integer id;
    private BigDecimal money;    //金额
    private String producer;    //发送方
    private String consumer;    //接收方
```

3.创建一个接口LuckymoneyRepository继承JpaRepository

JpaRepository<T,ID>：是访问数据库的关键接口；第一个参数是数据库的实体类，第二个参数是id的类型

4.在控制器LuckymoneyController定义一个LuckymoneyRepository变量repository，并加上@Autowired注解

功能1.获取红包列表

创建list()方法，返回值为`List<Luckymoney>`,调用LuckymoneyRepository的findAll()方法，设置处理请求方法为GET类型`@GetMapping("/luckymoneys")`

功能2.创建红包(发红包)；参数为发红包的人，红包的金额

创建create()方法，返回值为`Luckymoney`,参数有`@RequestParam("producer") String producer, @RequestParam("money") BigDecimal money`,在方法内创建一个Luckymoney对象，将获取到的producer，money值写入新创建的Luckymoney对象中，最后调用LuckymoneyRepository的save(S var1)方法，设置处理请求方法为POST类型`@PostMapping("/luckymoneys")`

(@RequestParam：将指定的请求参数赋值给方法中的形参)

功能3.通过id查询红包

创建findById()方法，返回值为`Luckymoney`,参数有`@PathVariable("id") Integer id`，最后调用LuckymoneyRepository的findById(ID var1)方法，判断当不存在此id时，返回null，因此还有调用orElse(null)方法，设置处理请求方法为GET类型`@GetMapping("/luckymoneys/{id}")`

(orElse(T other)：当值存在就会直接返回值，如果不存在会返回别的值（指定默认值）)

功能4.更新红包（领红包），id值，领红包的人

创建update()方法，返回值为`Luckymoney`,参数有`@PathVariable("id") Integer id,@RequestParam("consumer") String consumer`，先判断是否有此id，如果有，查看luckymoney的内容，并将id,consumer值写入luckymoney中，最后调用LuckymoneyRepository的save(S var1)方法，如果没有此id，直接返回null
```
    public Luckymoney update(@PathVariable("id") Integer id,@RequestParam("consumer") String consumer){
        Optional<Luckymoney> optional=repository.findById(id);
        if (optional.isPresent()){
            //get():查到luckymoney的内容
            Luckymoney luckymoney=optional.get();
            luckymoney.setId(id);
            luckymoney.setConsumer(consumer);
            return repository.save(luckymoney);
        }
        return null;
    }
```

(@PathVariable：可以将URL中占位符参数{xxx}绑定到处理器类的方法形参中)

功能5.事务处理

创建LuckymoneyService类，加上@Service注解，定义一个LuckymoneyRepository变量repository，并加上@Autowired注解，创建createTwo()方法，在方法内创建2个Luckymoney对象，将自定义的producer,money值写入新创建的Luckymoney对象中，都调用LuckymoneyRepository的save(S var1)方法，测试当其中一个Luckymoney对象出错时，另一个还会不会存入数据库，如果不能，则事务回滚成功

(@Service：用于标注业务层组件)

```
    @Autowired
    private LuckymoneyRepository repository;

    @Transactional
    public void createTwo(){
        Luckymoney luckymoney1=new Luckymoney();
        luckymoney1.setProducer("张三");
        luckymoney1.setMoney(new BigDecimal("123"));
        repository.save(luckymoney1);

        Luckymoney luckymoney2=new Luckymoney();
        luckymoney2.setProducer("张三");
        luckymoney2.setMoney(new BigDecimal("4567"));
        repository.save(luckymoney2);
    }
```

事务，是指数据库的事务，这里的@Transactional只是做了提交，回滚等事情，最终还是提交到数据库去的，所以首先数据库要能支持事务，引擎为mysiam不支持事务，所以事务回滚会失败，可改为：

1.数据库中创建的表的引擎是mysiam，将其改为InnoDB：ALTER TABLE table_name ENGINE=InnoDB;如果事务回滚仍然不起作用，重新建张表结构默认引擎为innodb的，即可支持事务的回滚操作

2.通过application.yml将引擎设置为innodb类型(不加database-platform值则默认为myisam引擎)
```
spring:
  profiles:
    active: run
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/luckymoney?serverTimezone=Asia/Shanghai
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
```

3.  可以通过SQL语句或者在dos命令查看引擎:`show engines`
    设置InnoDB为默认引擎：在配置文件my.ini中的 [mysqld] 下面加入`default-storage-engine=INNODB`
    重启mysql服务
    若修改完重启报错`Unknown storage engine 'InnoDB'`，检查是否有skip-innodb，如果有，注释掉。