package com.zqh.guice.hello;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * http://www.journaldev.com/2403/google-guice-dependency-injection-example-tutorial
 */
public class ClientApplication {

    public static void main(String[] args) {
        //Module负责bind接口到实现类
        Injector injector = Guice.createInjector(new MyApplicationModule());

        //Application中只注册接口
        //使用Module的Injector,获取到的实例, 其中的接口类已经被转换为Module中绑定的实现类了
        MyApplication app = injector.getInstance(MyApplication.class);

        //所以service中的Service是具体的实现类!
        app.sendMessage("Hi Pankaj", "pankaj@abc.com");
    }

}