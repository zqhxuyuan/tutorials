package com.zqh.midd.netty.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * http://blog.csdn.net/u013252773/article/details/21195593
 *
 * ChannelInboundHandler对从客户端发往服务器的报文进行处理，一般用来执行解码、读取客户端数据、进行业务处理等；
 * ChannelOutboundHandler对从服务器发往客户端的报文进行处理，一般用来进行编码、发送报文到客户端。
 *
 * ChannelInboundHandler按照注册的先后顺序执行；ChannelOutboundHandler按照注册的先后顺序逆序执行
 *
 * 当Client连接到Server后，会向Server发送一条消息。
 * Server端通过ChannelInboundHandler对Client发送的消息进行读取，通过ChannelOutboundHandler向client发送消息
 *
 * 简单来说: 对于Server而言, 读取Client为Inbound, 写入Client为Outbound
 *
 * ChannelInboundHandler之间的传递,通过调用ctx.fireChannelRead(msg)实现；
 * 调用ctx.write(msg) 将传递到ChannelOutboundHandler
 *
 * ChannelOutboundHandler在注册的时候需要放在最后一个ChannelInboundHandler之前，否则将无法传递到ChannelOutboundHandler
 */
public class HelloServer {
    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 注册两个OutboundHandler，执行顺序为注册顺序的逆序，所以应该是OutboundHandler2 OutboundHandler1
                            ch.pipeline().addLast(new OutboundHandler1());
                            ch.pipeline().addLast(new OutboundHandler2());
                            // 注册两个InboundHandler，执行顺序为注册顺序，所以应该是InboundHandler1 InboundHandler2
                            ch.pipeline().addLast(new InboundHandler1());
                            ch.pipeline().addLast(new InboundHandler2());

                            // InboundHandler1 ==> InboundHandler2 ==> OutboundHandler2 ==> OutboundHandler1
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();

            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     INFO - InboundHandler1.channelRead: ctx :io.netty.channel.DefaultChannelHandlerContext@ae9fecf
     INFO - InboundHandler2.channelRead: ctx :io.netty.channel.DefaultChannelHandlerContext@46fd1ccc
     Client said:Are you ok?
     INFO - OutboundHandler2.write
     INFO - OutboundHandler1.write
     INFO - InboundHandler1.channelReadComplete
     INFO - InboundHandler1.channelReadComplete
     */
    public static void main(String[] args) throws Exception {
        HelloServer server = new HelloServer();
        server.start(8000);
    }
}

