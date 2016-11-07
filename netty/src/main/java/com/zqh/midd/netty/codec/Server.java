package com.zqh.midd.netty.codec;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * http://blog.csdn.net/u013252773/article/details/21564301
 *
 * 测试coder 和 handler 的混合使用
 * Encoder、Decoder的本质也是Handler，它们的执行顺序、使用方法与Handler保持一致。
 *
 * 执行顺序是：Encoder 先注册的后执行，与OutboundHandler一致；
 * Decoder是先注册的先执行，与InboundHandler一致
 */
public class Server {
    public void start(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 都属于ChannelOutboundHandler，逆序执行
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new StringEncoder());

                            // 都属于ChannelIntboundHandler，按照顺序执行
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new BusinessHandler());
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

    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.start(8000);
    }
}

