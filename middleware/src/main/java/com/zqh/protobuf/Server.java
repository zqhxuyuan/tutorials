package com.zqh.protobuf;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.List;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcCallback;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.server.DuplexTcpServerPipelineFactory;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

/**
 * https://github.com/pjklauser/protobuf-rpc-pro
 * http://blog.csdn.net/zhu_tianwei/article/details/44065097
 */
public class Server {

    private static Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {

        PeerInfo serverInfo = new PeerInfo("127.0.0.1", 1234);

        // RPC payloads are uncompressed when logged - so reduce logging
        // 关闭 减少日志 或者com.googlecode.protobuf.pro.duplex.logging.nulllogger可以代替的，将不记录任何categoryperservicelogger。
        CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
        logger.setLogRequestProto(false);
        logger.setLogResponseProto(false);

        // 配置server
        DuplexTcpServerPipelineFactory serverFactory = new DuplexTcpServerPipelineFactory(serverInfo);
        // 设置线程池
        RpcServerCallExecutor rpcExecutor = new ThreadPoolCallExecutor(10, 10);
        serverFactory.setRpcServerCallExecutor(rpcExecutor);
        serverFactory.setLogger(logger);

        // 回调
        final RpcCallback<Message.Msg> clientResponseCallback = new RpcCallback<Message.Msg>() {
            @Override
            public void run(Message.Msg parameter) {
                log.info("接收  " + parameter);
            }
        };
        // 启动rpc事件监听
        RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();
        RpcConnectionEventListener listener = new RpcConnectionEventListener() {
            @Override
            public void connectionReestablished(RpcClientChannel clientChannel) {
                log.info("重新建立连接 " + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), clientResponseCallback);
            }

            @Override
            public void connectionOpened(RpcClientChannel clientChannel) {
                log.info("链接打开" + clientChannel);
                clientChannel.setOobMessageCallback(Message.Msg.getDefaultInstance(), clientResponseCallback);
            }

            @Override
            public void connectionLost(RpcClientChannel clientChannel) {
                log.info("链接断开" + clientChannel);
            }

            @Override
            public void connectionChanged(RpcClientChannel clientChannel) {
                log.info("链接改变" + clientChannel);
            }
        };

        rpcEventNotifier.setEventListener(listener);
        serverFactory.registerConnectionEventListener(rpcEventNotifier);
        //初始化netty
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(2, new RenamingThreadFactoryProxy("boss", Executors.defaultThreadFactory()));
        EventLoopGroup workers = new NioEventLoopGroup(16, new RenamingThreadFactoryProxy("worker", Executors.defaultThreadFactory()));
        bootstrap.group(boss, workers);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);
        bootstrap.childOption(ChannelOption.SO_RCVBUF, 1048576);
        bootstrap.childOption(ChannelOption.SO_SNDBUF, 1048576);
        //bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);

        bootstrap.childHandler(serverFactory);
        bootstrap.localAddress(serverInfo.getPort());

        //关闭释放资源
        CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(boss);
        shutdownHandler.addResource(workers);
        shutdownHandler.addResource(rpcExecutor);

        bootstrap.bind();
        log.info("启动监听： " + bootstrap);

        //定时向客户端发送消息
        while (true) {
            List<RpcClientChannel> clients = serverFactory.getRpcClientRegistry().getAllClients();
            for (RpcClientChannel client : clients) {
                //创建消息
                Message.Msg msg = Message.Msg.newBuilder().setContent("Server "+ serverFactory.getServerInfo() + " OK@" + System.currentTimeMillis()).build();

                ChannelFuture oobSend = client.sendOobMessage(msg);
                if (!oobSend.isDone()) {
                    log.info("Waiting for completion.");
                    oobSend.syncUninterruptibly();
                }
                if (!oobSend.isSuccess()) {
                    log.warn("OobMessage send failed." + oobSend.cause());
                }

            }
            log.info("Sleeping 5s before sending request to all clients.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
