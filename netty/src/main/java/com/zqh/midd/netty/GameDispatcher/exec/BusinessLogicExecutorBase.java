package com.zqh.midd.netty.GameDispatcher.exec;

import io.netty.channel.Channel;

/**
 * 执行业务逻辑的基类
 * 实现Runnable接口
 * 
 * @author xingchencheng
 *
 */

public class BusinessLogicExecutorBase implements Runnable {

	protected Channel channel;
	
	protected Object msgObject;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Object getMsgObject() {
		return msgObject;
	}

	public void setMsgObject(Object msgObject) {
		this.msgObject = msgObject;
	}

	public void run() {
		
	}
	
}