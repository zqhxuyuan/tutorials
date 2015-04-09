package com.zqh.midd.netty.GameDispatcher.exec;

import com.zqh.midd.netty.GameDispatcher.annotation.UserMsgAndExecAnnotation;
import com.zqh.midd.netty.GameDispatcher.msg.MsgType;
import com.zqh.midd.netty.GameDispatcher.msg.UserAddRequest;
import com.zqh.midd.netty.GameDispatcher.msg.UserAddResponse;

/**
 * 具体的业务逻辑
 * 实现加法
 * 
 * @author xingchencheng
 *
 */

@UserMsgAndExecAnnotation(msgType = MsgType.ADD)
public class UserAddExecutor extends BusinessLogicExecutorBase {

	public void run() {
		UserAddResponse response = new UserAddResponse();
		
		if (this.msgObject instanceof UserAddRequest) {
			UserAddRequest request = (UserAddRequest) this.msgObject;
			double result = request.getLeftNumber() + request.getRightNumber();
			response.setResult(result);
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
		}
		
		System.out.println("服务端处理结果：" + response.getResult());
		channel.writeAndFlush(response);
	}

}
