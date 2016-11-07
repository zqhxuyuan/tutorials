package com.zqh.midd.netty.GameDispatcher.msg;

/**
 * 响应消息基类
 * 
 * @author xingchencheng
 *
 */

public class ResponseMsgBase extends AbstractMsg {
	
	protected boolean isSuccess;
	
	public ResponseMsgBase() {
	}
	
	public ResponseMsgBase(short type) {
		super((short) (0 - type));
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	
}
