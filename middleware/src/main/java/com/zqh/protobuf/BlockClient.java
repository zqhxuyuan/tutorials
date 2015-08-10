package com.zqh.protobuf;

import com.zqh.protobuf.Message.*;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.google.protobuf.BlockingService;
import com.google.protobuf.ExtensionRegistry;
import com.googlecode.protobuf.pro.duplex.CleanShutdownHandler;
import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.RpcConnectionEventNotifier;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.client.RpcClientConnectionWatchdog;
import com.googlecode.protobuf.pro.duplex.execute.RpcServerCallExecutor;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import com.googlecode.protobuf.pro.duplex.listener.RpcConnectionEventListener;
import com.googlecode.protobuf.pro.duplex.logging.CategoryPerServiceLogger;
import com.googlecode.protobuf.pro.duplex.util.RenamingThreadFactoryProxy;

public class BlockClient {
    private static RpcClientChannel channel = null;

    private static Logger log = LoggerFactory.getLogger(BlockClient.class);

    public static void main(String[] args) throws Exception {
        PeerInfo client = new PeerInfo("127.0.0.1", 54321);
        PeerInfo server = new PeerInfo("127.0.0.1", 12345);

        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
        // force the use of a local port
        // - normally you don't need this
        clientFactory.setClientInfo(client);

        ExtensionRegistry r = ExtensionRegistry.newInstance();
        Message.registerAllExtensions(r);
        clientFactory.setExtensionRegistry(r);

        clientFactory.setConnectResponseTimeoutMillis(10000);
        RpcServerCallExecutor rpcExecutor = new ThreadPoolCallExecutor(3, 10);
        clientFactory.setRpcServerCallExecutor(rpcExecutor);

        // RPC payloads are uncompressed when logged - so reduce logging
        CategoryPerServiceLogger logger = new CategoryPerServiceLogger();
        logger.setLogRequestProto(false);
        logger.setLogResponseProto(false);
        clientFactory.setRpcLogger(logger);

        // Set up the event pipeline factory.
        // setup a RPC event listener - it just logs what happens
        RpcConnectionEventNotifier rpcEventNotifier = new RpcConnectionEventNotifier();

        final RpcConnectionEventListener listener = new RpcConnectionEventListener() {

            @Override
            public void connectionReestablished(RpcClientChannel clientChannel) {
                log.info("connectionReestablished " + clientChannel);
                channel = clientChannel;
            }

            @Override
            public void connectionOpened(RpcClientChannel clientChannel) {
                log.info("connectionOpened " + clientChannel);
                channel = clientChannel;
            }

            @Override
            public void connectionLost(RpcClientChannel clientChannel) {
                log.info("connectionLost " + clientChannel);
            }

            @Override
            public void connectionChanged(RpcClientChannel clientChannel) {
                log.info("connectionChanged " + clientChannel);
                channel = clientChannel;
            }
        };
        rpcEventNotifier.addEventListener(listener);
        clientFactory.registerConnectionEventListener(rpcEventNotifier);
        //注册服务 reply阻塞服务，用于反馈
        BlockingService blockingReplyService = ReplyService.newReflectiveBlockingService(new BlockReplyService());
        clientFactory.getRpcServiceRegistry().registerService(blockingReplyService);

        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup workers = new NioEventLoopGroup(16, new RenamingThreadFactoryProxy("workers", Executors.defaultThreadFactory()));

        bootstrap.group(workers);
        bootstrap.handler(clientFactory);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);

        RpcClientConnectionWatchdog watchdog = new RpcClientConnectionWatchdog(clientFactory, bootstrap);
        rpcEventNotifier.addEventListener(watchdog);
        watchdog.start();

        CleanShutdownHandler shutdownHandler = new CleanShutdownHandler();
        shutdownHandler.addResource(workers);
        shutdownHandler.addResource(rpcExecutor);

        clientFactory.peerWith(server, bootstrap);

        while (true && channel != null) {
            RpcService.BlockingInterface blockingService = RpcService.newBlockingStub(channel);
            final ClientRpcController controller = channel.newRpcController();
            controller.setTimeoutMs(0);

            Params params = Params.newBuilder().setKey("name").setValue("jack").build();
            Request request = Request.newBuilder().setServiceName("UserService").setMethodName("insert").setParams(params).build();
            //阻塞调用
            blockingService.call(controller, request);
            Thread.sleep(100000);
        }
    }

}
