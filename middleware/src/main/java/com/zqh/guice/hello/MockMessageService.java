package com.zqh.guice.hello;

public class MockMessageService implements MessageService{

    public boolean sendMessage(String msg, String receipient) {
        return true;
    }

}