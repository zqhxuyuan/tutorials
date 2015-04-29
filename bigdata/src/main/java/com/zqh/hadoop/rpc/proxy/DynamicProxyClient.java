package com.zqh.hadoop.rpc.proxy;

import com.zqh.hadoop.rpc.java.Echo;
import com.zqh.hadoop.rpc.java.RemoteEcho;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class DynamicProxyClient {

    public static void main(String[] args) throws Exception{
        Echo realSubject = new RemoteEcho();
        ClassLoader loader = realSubject.getClass().getClassLoader();// 获得被代理类的类加载器, 使得JVM能够加载并找到被代理类的内部结构,
        Class<?>[] interfaces = realSubject.getClass().getInterfaces();// 以及已实现的interface
        InvocationHandler handler = new DynamicProxy(realSubject); // 用被代理类的实例创建动态代理类的实例, 用于真正调用处理程序
        // 获得代理的实例: 返回实现了被代理类所实现的所有接口的Object对象, 即动态代理, 需要强制转型
        Echo proxy = (Echo) Proxy.newProxyInstance(loader, interfaces, handler); // 返回的代理类比如$Proxy0 3
        proxy.echo("zqh");// 调用代理方法
        System.out.println(proxy.getClass().getName()); // 打印出该代理实例的名称 $Proxy0
    }
}
