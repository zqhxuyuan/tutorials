package com.zqh.midd.netty.GameDispatcher.client;

import com.zqh.midd.netty.GameDispatcher.msg.MsgType;
import com.zqh.midd.netty.GameDispatcher.msg.UserAddRequest;
import com.zqh.midd.netty.GameDispatcher.msg.UserMultiRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * 客户端handler
 * @author xingchencheng
 *
 */

public class ClientDispatcherHandler extends SimpleChannelInboundHandler<Object>{

	private static CountDownLatch latch;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msgObject)
			throws Exception {

		// 单纯的打印响应消息
		System.out.println(msgObject.toString());
		latch.countDown();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("connection active!");
		// 连接完成后便运行控制台
		Thread consoleThread = new Thread(new Console(ctx.channel()));
		consoleThread.setDaemon(true);
		consoleThread.start();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		cause.printStackTrace();
		ctx.close();
		System.exit(-1);
	}

	// 客户端命令控制台
	class Console implements Runnable {
		
		private Channel channel;
		
		private Scanner scanner;
		
		public Console(Channel channel) {
			this.channel = channel;
			this.scanner = new Scanner(System.in);
		}

		public void run() {
			int type;
			double p1;
			double p2;
			
			while (true) {
				try {
					System.out.println("请输入请求消息的类型：\n1为加法请求\n2为乘法请求");
					type = scanner.nextInt();
					System.out.println("请输入第一个操作数");
					p1 = scanner.nextDouble();
					System.out.println("请输入第二个操作数");
					p2 = scanner.nextDouble();
				} catch (Exception e) {
					System.err.println("异常");
					e.printStackTrace();
					continue;
				}
				
				try {
					if (type == MsgType.ADD) {
						UserAddRequest addReq = new UserAddRequest();
						addReq.setLeftNumber(p1);
						addReq.setRightNumber(p2);
						
						latch = new CountDownLatch(1);
						this.channel.writeAndFlush(addReq);
						latch.await();
					} else if (type == MsgType.MULTI) {
						UserMultiRequest multiReq = new UserMultiRequest();
						multiReq.setLeftNumber(p1);
						multiReq.setRightNumber(p2);
						
						latch = new CountDownLatch(1);
						this.channel.writeAndFlush(multiReq);
						latch.await();
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}

			}
		}
		
	}
}