package com.github.NoahShen.jue.util;

import java.nio.ByteBuffer;

/**
 * 字节工具类，转换方式通过ByteBuffer进行，
 * 统一使用ByteBuffer默认的字节顺序，防止字节顺序不同产生的问题
 * 
 * @author noah
 * 
 */
public class ByteUtil {

	/**
	 * int转byte[]
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] int2byte(int i) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(i);
		buffer.flip();
		return buffer.array();
	}

	/**
	 * byte[]转int
	 * 
	 * @param b
	 * @return
	 */
	public static int byte2int(byte[] b) {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(b);
		buffer.flip();
		return buffer.getInt();
	}

	/**
	 * long转byte[]
	 * 
	 * @param l
	 * @return
	 */
	public static byte[] long2byte(long l) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.putLong(l);
		buffer.flip();
		return buffer.array();
	}

	/**
	 * byte[]转long
	 * @param b
	 * @return
	 */
	public static long byte2long(byte[] b) {
		ByteBuffer buffer = ByteBuffer.allocate(8);
		buffer.put(b);
		buffer.flip();
		return buffer.getLong();
	}
	
	public static byte[] getBytesFromBuffer(ByteBuffer buffer) {
		int size = buffer.remaining();
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		return bytes;
	}
}