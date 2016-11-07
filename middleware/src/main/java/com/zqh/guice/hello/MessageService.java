package com.zqh.guice.hello;

/**
 * Created by zhengqh on 15/12/6.
 */
public interface MessageService {

    boolean sendMessage(String msg, String receipient);
}