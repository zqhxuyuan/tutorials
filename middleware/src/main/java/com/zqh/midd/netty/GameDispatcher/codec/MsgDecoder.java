package com.zqh.midd.netty.GameDispatcher.codec;

import com.zqh.midd.netty.GameDispatcher.util.ClassUtil;
import com.zqh.midd.netty.GameDispatcher.util.GsonUtil;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 解码器
 * 客户端和服务端均有使用
 * 0-1字节表示整个消息的长度（单位：字节）
 * 2-3字节代表消息类型，对应annotation
 * 余下的是消息的json字符串（UTF-8编码）
 * 
 * @author xingchencheng
 */
public class MsgDecoder extends ByteToMessageDecoder {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> list) throws Exception {
		if (buf.readableBytes() < 2) {
			return;
		}
		short jsonBytesLength = (short) (buf.readShort() - 2);
		short type = buf.readShort();
		
		byte[] tmp = new byte[jsonBytesLength];
		buf.readBytes(tmp);
		String json = new String(tmp, "UTF-8");
		
		Class<?> clazz = ClassUtil.getMsgClassByType(type);
        Gson gson = GsonUtil.getGson();
		Object msgObj = gson.fromJson(json, clazz);
		list.add(msgObj);
	}
}
