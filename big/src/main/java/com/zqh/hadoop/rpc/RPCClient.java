package com.zqh.hadoop.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.net.InetSocketAddress;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RPCClient {

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        ClientProtocol proxy = RPC.getProxy(ClientProtocol.class,
                ClientProtocol.versionID,
                new InetSocketAddress(RPCServer.addr,RPCServer.port),
                conf);
        String echo = proxy.echo("hello");
        System.out.println("Client:" + echo);
    }
}
