package com.zqh.guice.provider;

public class MockConnection {

    public void connect(){
        System.out.println("Connecting to the mock database");
    }

    public void disConnect(){
        System.out.println("Dis-connecting from the mock database");
    }

}
