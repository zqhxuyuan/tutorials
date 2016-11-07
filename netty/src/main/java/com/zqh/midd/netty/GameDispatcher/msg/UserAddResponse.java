package com.zqh.midd.netty.GameDispatcher.msg;

import com.zqh.midd.netty.GameDispatcher.annotation.UserMsgAndExecAnnotation;

/**
 * 加法请求响应消息类
 * 注意msgType是加法请求类的msgType的负数
 * 
 * @author xingchencheng
 *
 */

@UserMsgAndExecAnnotation(msgType = -MsgType.ADD)
public class UserAddResponse extends ResponseMsgBase {
	
	private double result;
	
	public UserAddResponse() {
		super(MsgType.ADD);
	}

	public double getResult() {
		return result;
	}

	public void setResult(double result) {
		this.result = result;
	}
	
	@Override
	public String toString() {
		return "result: " + this.result;
	}
}