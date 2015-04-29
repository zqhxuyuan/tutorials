package com.zqh.hadoop.rpc.java;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RPCServer {

    public static void main(String[] args) throws Exception{
        Server server = new RPC.RPCServer();
        server.register(Echo.class, RemoteEcho.class); // 1.向服务器注册接口和实现类
        server.start(); // 1.启动服务器
    }
}
