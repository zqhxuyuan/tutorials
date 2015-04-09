package com.zqh.midd.netty.multiprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInitHandler extends ChannelInboundHandlerAdapter {
    private static Logger   logger  = LoggerFactory.getLogger(ClientInitHandler.class);

    private Person person;

    public ClientInitHandler(Person person){
        this.person = person;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("ClientInitHandler.channelActive");
        ctx.write(person);
        ctx.flush();
    }
}