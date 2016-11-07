package com.yxl.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javassist.expr.NewArray;

import com.yxl.provider.DemoService;
import com.yxl.util.SpringBeanHelper;

//消费者线程
public class LogicThread implements Runnable{

	private static AtomicInteger ai = new AtomicInteger(1);
	
	@Override
	public void run() {
        //获取的是consumer.xml中的demoService Bean
		DemoService demoService = (DemoService) SpringBeanHelper.getBean("demoService");

        //RPC调用
        String hello = demoService.sayHello("world");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(new Date()) + " = " + ai.getAndIncrement() + " : " + hello); 
	}

}
