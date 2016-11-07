package com.zqh.midd.netty.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientInitHandler extends ChannelInboundHandlerAdapter {
    private static Logger	logger	= LoggerFactory.getLogger(ClientInitHandler.class);
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("HelloClientIntHandler.channelActive");
        Person person = new Person();
        person.setName("guowl");
        person.setSex("man");
        person.setAge(30);
        ctx.write(person);
        ctx.flush();
    }
}
