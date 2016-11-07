package com.zqh.midd.netty.GameDispatcher.client;

import com.zqh.midd.netty.GameDispatcher.codec.MsgDecoder;
import com.zqh.midd.netty.GameDispatcher.codec.MsgEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.oio.OioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

/**
 * 客户端启动类
 * @author xingchencheng
 *
 */

public class ClientBootstrapStarter {
	
	public static void start(String remote, int remotePort) {
		OioEventLoopGroup group = new OioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group);
			bootstrap.channel(OioSocketChannel.class);
			bootstrap.handler(new ChannelInitializer<Channel>() {
				@Override
				protected void initChannel(Channel channel) throws Exception {
					
					ChannelPipeline pipeline = channel.pipeline();
					// pipeline.addLast("logger", new LoggingHandler(LogLevel.DEBUG));

					pipeline.addLast("encoder", new MsgEncoder());
					pipeline.addLast("LengthFieldBasedFrameDecoder", 
							new LengthFieldBasedFrameDecoder(65 * 1024, 0, 2));
					pipeline.addLast("decoder", new MsgDecoder());
					pipeline.addLast("handler", new ClientDispatcherHandler());
				}
			});
			
			ChannelFuture future = bootstrap.connect(
					new InetSocketAddress(remote, remotePort)).sync();
			
			if (future.isSuccess() == false) {
				return;
			}
		
			future.channel().closeFuture().sync();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
	}
}