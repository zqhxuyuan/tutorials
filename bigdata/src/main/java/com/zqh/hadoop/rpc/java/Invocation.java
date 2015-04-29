package com.zqh.hadoop.rpc.java;

import java.io.Serializable;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class Invocation implements Serializable {
    private Class interfaces;   //要执行的接口 比如此例的Echo接口, 不过实际执行的是远程对象RemoteEcho
    private Method method;      //要执行的接口的方法 比如RemoteEcho的echo方法
    private Object[] params;    //方法传递的参数 比如echo方法的参数String echo
    private Object result;      //执行方法返回的结果 比如 echo 方法的返回值为 String: from remote:echo

    public Class getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(Class interfaces) {
        this.interfaces = interfaces;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}