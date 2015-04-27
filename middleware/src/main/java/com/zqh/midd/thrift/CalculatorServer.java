package com.zqh.midd.thrift;

import com.zqh.midd.thrift.server.CalculatorService;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

/**
 * Created by zqhxuyuan on 15-4-27.
 */
public class CalculatorServer {

    public static void start(CalculatorService.Processor<CalculatorServiceHandler> processor) throws Exception{
        TServerTransport serverTransport = new TServerSocket(9090);		// 服务端Socket
        TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(processor));
        System.out.println("Starting the simple server...");
        server.serve();
    }
    public static void main(String[] args) throws Exception{
        start(new CalculatorService.Processor<CalculatorServiceHandler>(new CalculatorServiceHandler()));
    }
}
