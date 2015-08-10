package com.zqh.protobuf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.RpcController;
import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.RpcClientChannel;
import com.googlecode.protobuf.pro.duplex.execute.ServerRpcController;

import com.zqh.protobuf.Message.*;

/**
 * 阻塞接口实现
 */
public  class BlockRpcService implements RpcService.BlockingInterface{

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Response call(RpcController controller, Request request) throws ServiceException {
        if ( controller.isCanceled() ) {
            return null;
        }
        log.info("接收到数据：");
        log.info("serviceName : "+request.getServiceName());
        log.info("methodName : "+request.getMethodName());
        log.info("params : "+request.getParams());

        RpcClientChannel channel = ServerRpcController.getRpcChannel(controller);
        ReplyService.BlockingInterface clientService = ReplyService.newBlockingStub(channel);
        ClientRpcController clientController = channel.newRpcController();
        clientController.setTimeoutMs(3000);
        //调用过程反馈消息
        Msg msg = Msg.newBuilder().setContent("success.").build();
        clientService.call(clientController, msg);
        Response response = Response.newBuilder().setCode(0).setMsg("处理完成").setData("server hello").build();
        return response;
    }

}