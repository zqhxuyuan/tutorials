package com.zqh.hadoop.rpc.java;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class Client {
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private String host;
    private int port;

    public Client(String host, int port){
        host = host;
        port = port;
    }

    public void invoke(Invocation invo) throws Exception {
        socket = new Socket(host, port);
        oos = new ObjectOutputStream(socket.getOutputStream());
        // 7. 客户端向服务器写数据. 因为客户端不能直接调用远程对象的方法(在不同JVM上),
        // 可以通过传递带有接口, 方法, 参数的Invocation对象给服务器, 让服务器解析出对象并真正调用方法
        oos.writeObject(invo);
        // 11. 接收服务器返回的数据Invocation对象, 对象里也含有方法的执行结果
        ois = new ObjectInputStream(socket.getInputStream());
        Invocation result = (Invocation) ois.readObject();
        invo.setResult(result.getResult());
    }
}