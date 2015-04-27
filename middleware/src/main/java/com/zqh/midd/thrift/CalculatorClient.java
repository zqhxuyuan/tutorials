package com.zqh.midd.thrift;

import com.zqh.midd.thrift.server.CalculatorService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * Created by zqhxuyuan on 15-4-27.
 */
public class CalculatorClient {

    public static void main(String[] args) throws Exception{
        TTransport transport = new TSocket("localhost", 9090);
        transport.open();
        TProtocol protocol = new TBinaryProtocol(transport);
        CalculatorService.Client client = new CalculatorService.Client(protocol);
        System.out.println(client.add(100, 200));
        transport.close();
    }
}
