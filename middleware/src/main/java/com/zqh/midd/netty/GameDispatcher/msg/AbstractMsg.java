package com.zqh.midd.netty.GameDispatcher.msg;

/**
 * 抽象消息类
 * 
 * @author xingchencheng
 *
 */

public abstract class AbstractMsg {
	protected short type;
	
	public AbstractMsg() {
	}
	
	public AbstractMsg(short type) {
		this.type = type;
	}

	public short getType() {
		return type;
	}
	
}
