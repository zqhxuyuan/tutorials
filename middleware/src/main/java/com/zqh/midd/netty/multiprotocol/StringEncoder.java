package com.zqh.midd.netty.multiprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;


public class StringEncoder extends MessageToByteEncoder<Person> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Person msg, ByteBuf out) throws Exception {
        // 转成字符串：name:xx;age:xx;sex:xx;
        StringBuffer sb = new StringBuffer();
        sb.append("name:").append(msg.getName()).append(";");
        sb.append("age:").append(msg.getAge()).append(";");
        sb.append("sex:").append(msg.getSex()).append(";");
        out.writeBytes(sb.toString().getBytes());
    }
}