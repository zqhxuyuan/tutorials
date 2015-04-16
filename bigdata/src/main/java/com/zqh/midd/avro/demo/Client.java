package com.zqh.midd.avro.demo;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.avro.ipc.NettyTransceiver;
import org.apache.avro.ipc.specific.SpecificRequestor;

import example.proto.Greeting;
import example.proto.HelloWorld;

public class Client {  
	  
    public static void main(String[] args) throws IOException {  
        NettyTransceiver client = new NettyTransceiver(new InetSocketAddress(65000));  
  
        HelloWorld proxy = (HelloWorld) SpecificRequestor.getClient(HelloWorld.class, client);  
        System.out.println("Client built, got proxy");  
          
        Greeting greeting = new Greeting("how are you");  
        System.out.println("Calling proxy.hello with message: " + greeting.toString());  
        System.out.println("Result: " +proxy.hello(greeting));  
        // cleanup  
        client.close();  
    }  
}  