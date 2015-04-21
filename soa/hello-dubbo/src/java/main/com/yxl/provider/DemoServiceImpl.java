package com.yxl.provider;

//服务提供者
public class DemoServiceImpl implements DemoService {

	@Override
	public String sayHello(String name) {
		return "Hello " + name;
	}

}
