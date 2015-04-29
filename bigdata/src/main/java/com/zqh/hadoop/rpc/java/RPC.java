package com.zqh.hadoop.rpc.java;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zqhxuyuan on 15-4-28.
 */
public class RPC {

    public static <T> T getProxy(final Class<T> clazz,String host,int port) {
        final Client client = new Client(RPCConstant.host,RPCConstant.port);

        InvocationHandler handler = new InvocationHandler() {

            // 5. 当客户端调用生成的代理对象的方法, 实际上调用的是该回调方法
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Invocation invo = new Invocation(); // 封装Invocation对象, 要调用的接口, 接口的方法, 参数
                invo.setInterfaces(clazz);
                invo.setMethod(new com.zqh.hadoop.rpc.java.Method(method.getName(), method.getParameterTypes()));
                invo.setParams(args);
                client.invoke(invo); // 6. 客户端向服务器发送Invocation对象

                return invo.getResult(); // 12. 回调方法invoke()结束, 返回远程方法的执行结果
            }
        };

        // 3. 生成接口的代理对象, 传入回调对象InvocationHandler.在调用接口的方法时, 会调用回调方法
        return (T) Proxy.newProxyInstance(RPC.class.getClassLoader(), new Class[]{clazz}, handler);
    }

    public static class RPCServer implements Server{
        private boolean isRuning = false;
        private Listener listener;
        private Map<String ,Object> serviceEngine = new HashMap<String, Object>();

        public void register(Class interfaceDefiner, Class impl) throws Exception{
            this.serviceEngine.put(interfaceDefiner.getName(), impl.newInstance());
        }

        public void start() {
            listener = new Listener(this);
            this.isRuning = true;
            listener.start(); // 1.启动服务器,监听器是个线程类,会调用run()
        }

        @Override
        public void stop() {
            listener.stop();
            isRuning = false;
        }

        public void call(Invocation invo) throws Exception{
            Object obj = serviceEngine.get(invo.getInterfaces().getName()); //根据接口名,找到对应的处理类(实现类)
            Method m = obj.getClass().getMethod(invo.getMethod().getMethodName(), invo.getMethod().getParams());
            Object result = m.invoke(obj, invo.getParams()); // 9. 利用反射,调用方法,返回值设置到Invocation对象中
            invo.setResult(result);
        }

        @Override
        public boolean isRunning() {
            return isRuning;
        }

        @Override
        public int getPort() {
            return RPCConstant.port;
        }
    }
}