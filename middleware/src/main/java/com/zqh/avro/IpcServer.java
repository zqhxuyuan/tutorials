package com.zqh.avro;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyServer;
import org.apache.avro.ipc.Server;
import org.apache.avro.ipc.specific.SpecificResponder;

import example.proto.HelloWorld;

public class IpcServer {  
    private Server server;  
  
    public void startServer() throws IOException, InterruptedException {  
        server = new NettyServer(new SpecificResponder(HelloWorld.class,  
                new HelloWorldImpl()), new InetSocketAddress(65000));
    }  
  
    public void stopServer() {  
        server.close();  
    }  
  
    public static void main(String[] args) throws IOException, InterruptedException {  
        System.out.println("Starting server");  
        IpcServer ipcServer = new IpcServer();  
        ipcServer.startServer();  
        System.out.println("Server started");  
    }  
}  