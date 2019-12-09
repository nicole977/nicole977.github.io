---
title: final static与static
categories: 杂七杂八
date: 2019-05-30 22:09:41
---

刚刚有个小伙伴在群里问这个问题，两个demo就相差一个final,结果为什么不一样？我一时没答出来==

两个demo

1.demo1

```
package com.demo;

public class demo1 {
	public static void main(String[] args) {
		System.out.println(B.c);
	}
}

class A{
	static {
		System.out.println("A");
	}
}

class B{
	static {
		System.out.println("B");
	}
	public final static String c="C";
}
```
输出：C

2.demo2

```
package com.demo;

public class demo2 {
	public static void main(String[] args) {
		System.out.println(B.c);
	}
}

class A{
	static {
		System.out.println("A");
	}
}

class B{
	static {
		System.out.println("B");
	}
	public static String c="C";
}
```
输出：BC

两个demo的区别只在变量c一个有被final修饰，一个没有被final修饰

demo1没有运行静态方法，是因为变量c被final static修饰，被final static修饰的变量在编译时就已经被引用到各个类中去了，因此demo1去调用B类的变量c时，B类不需要再实例化就可直接调用c；而demo2的变量c没有被final修饰，被调用时B类要实例化，一实例化静态代码块就会执行，因此demo2输出的结果是BC

( ﹁ ﹁ ) ~→我之前一直以为只要是静态代码块就都会执行来着......