package com.zqh.hadoop.rpc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class DynamicProxy implements InvocationHandler{

    Object obj = null; // 被代理类的实例

    public DynamicProxy(Object obj) {
        this.obj = obj; // 将被代理类的实例传进动态代理类的构造函数中
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(this.obj, args);  // 通过反射调用被代理对象的方法
        return result;

    }
}
