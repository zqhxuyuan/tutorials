package com.zqh.thrift;

import com.zqh.midd.thrift.server.CalculatorService;
import org.apache.thrift.TException;

/**
 * Created by zqhxuyuan on 15-4-27.
 */
public class CalculatorServiceHandler implements CalculatorService.Iface {

    @Override
    public int add(int n1, int n2) throws TException {
        return n1 + n2;
    }
}
