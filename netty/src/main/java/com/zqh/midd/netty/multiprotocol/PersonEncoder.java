package com.zqh.midd.netty.multiprotocol;

import com.zqh.midd.netty.ByteObjConverter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PersonEncoder extends MessageToByteEncoder<Person>  {

    @Override
    protected void encode(ChannelHandlerContext ctx, Person msg, ByteBuf out) throws Exception {
        out.writeBytes(ByteObjConverter.ObjectToByte(msg));
    }
}