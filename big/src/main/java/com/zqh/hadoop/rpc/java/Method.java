package com.zqh.hadoop.rpc.java;

import java.io.Serializable;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class Method implements Serializable {
    private String methodName;
    private Class[] params;

    public Method(String name, Class<?>[] parameterTypes) {
        this.methodName = name;
        this.params = parameterTypes;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParams() {
        return params;
    }

    public void setParams(Class[] params) {
        this.params = params;
    }
}