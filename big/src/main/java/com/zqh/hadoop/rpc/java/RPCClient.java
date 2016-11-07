package com.zqh.hadoop.rpc.java;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RPCClient {

    public static void main(String[] args) {
        Echo echo = RPC.getProxy(Echo.class, "127.0.0.1", 20382); // 3. 生成接口Echo的代理类$Proxy0
        String res = echo.echo("hello,hello"); // 4. 像使用本地的程序一样来调用Echo中的echo方法
    }
}
