package com.zqh.midd.netty.GameDispatcher.exec;

import com.zqh.midd.netty.GameDispatcher.annotation.UserMsgAndExecAnnotation;
import com.zqh.midd.netty.GameDispatcher.msg.MsgType;
import com.zqh.midd.netty.GameDispatcher.msg.UserMultiRequest;
import com.zqh.midd.netty.GameDispatcher.msg.UserMultiResponse;

/**
 * 具体的业务逻辑
 * 实现乘法
 * 
 * @author xingchencheng
 *
 */

@UserMsgAndExecAnnotation(msgType = MsgType.MULTI)
public class UserMultiExecutor extends BusinessLogicExecutorBase {
	public void run() {
		UserMultiResponse response = new UserMultiResponse();
		
		if (this.msgObject instanceof UserMultiRequest) {
			UserMultiRequest request = (UserMultiRequest) this.msgObject;
			double result = request.getLeftNumber() * request.getRightNumber();
			response.setResult(result);
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
		}
		
		System.out.println("服务端处理结果：" + response.getResult());
		channel.writeAndFlush(response);
	}
}
