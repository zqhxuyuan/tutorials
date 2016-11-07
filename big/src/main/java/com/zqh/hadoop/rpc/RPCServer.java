package com.zqh.hadoop.rpc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RPCServer {

    public static final String addr = "localhost";
    public static final int port = 9999;

    public static void main(String[] args) throws Exception{
        Configuration conf = new Configuration();
        RPC.Server server = new RPC.Builder(conf).setProtocol(ClientProtocol.class)
                .setInstance(new ClientProtocolImpl())
                .setBindAddress(addr).setPort(port)
                .setNumHandlers(5)
                .build();

        server.start();
    }
}
