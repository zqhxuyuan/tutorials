package com.zqh.midd.netty.GameDispatcher.server;

import com.zqh.midd.netty.GameDispatcher.Dispatcher;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 服务端handler
 * @author xingchencheng
 */
public class ServerDispatcherHandler extends SimpleChannelInboundHandler<Object>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msgObject) throws Exception {
		// 分发消息给对应的消息处理器
		Dispatcher.submit(ctx.channel(), msgObject);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("accept a active connection!");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		ctx.close();
		cause.printStackTrace();
	}

}