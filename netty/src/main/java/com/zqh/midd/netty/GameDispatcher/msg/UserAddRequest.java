package com.zqh.midd.netty.GameDispatcher.msg;

import com.zqh.midd.netty.GameDispatcher.annotation.UserMsgAndExecAnnotation;

/**
 * 加法请求消息类
 * 
 * @author xingchencheng
 *
 */

@UserMsgAndExecAnnotation(msgType = MsgType.ADD)
public class UserAddRequest extends RequestMsgBase {

	private double leftNumber;
	private double RightNumber;
	
	public UserAddRequest() {
		super(MsgType.ADD);
	}

	public double getLeftNumber() {
		return leftNumber;
	}

	public void setLeftNumber(double leftNumber) {
		this.leftNumber = leftNumber;
	}

	public double getRightNumber() {
		return RightNumber;
	}

	public void setRightNumber(double rightNumber) {
		RightNumber = rightNumber;
	}

}