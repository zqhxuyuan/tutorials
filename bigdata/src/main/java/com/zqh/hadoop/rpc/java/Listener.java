package com.zqh.hadoop.rpc.java;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class Listener extends Thread {
    private ServerSocket socket;
    private Server server;

    public Listener(Server server){
        this.server = server;
    }

    public void run() {
        try{
            socket = new ServerSocket(server.getPort()); // 2. 创建ServerSocket, 接受客户端的连接
            while (server.isRunning()) {
                Socket client = socket.accept();
                // 8. 接收客户端传递的Invocation对象, 里面包含了客户端想要调用的接口, 方法, 参数
                ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
                Invocation invo = (Invocation) ois.readObject();
                // 9. 让服务器调用真正的目标方法
                server.call(invo);
                // 10. 往客户端写回数据, 同样给客户端发送Invocation对象
                ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
                oos.writeObject(invo);
            }
        }catch (Exception e){}
    }
}