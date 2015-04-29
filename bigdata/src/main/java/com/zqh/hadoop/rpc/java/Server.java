package com.zqh.hadoop.rpc.java;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public interface Server {
    public void start(); // 启动服务器
    public void stop(); // 停止服务
    public void register(Class interfaceDefiner,Class impl) throws Exception; // 注册一个接口和对应的实现类
    public void call(Invocation invo) throws Exception; // 执行Invocation指定的接口的方法名
    public boolean isRunning();// 返回服务器的状态
    public int getPort();// 返回服务器使用的端口
}