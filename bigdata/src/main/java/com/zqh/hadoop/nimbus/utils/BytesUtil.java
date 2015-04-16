package com.zqh.hadoop.nimbus.utils;

import java.nio.charset.Charset;

public class BytesUtil {

	private static final Charset charSet = Charset.forName("UTF-8");

	public static final byte[] TRUE_BYTES = BytesUtil.toBytes("true");
	public static final byte[] FALSE_BYTES = BytesUtil.toBytes("false");
	public static final byte[] EMPTY_BYTES = new byte[0];
	
	public static String toString(byte[] bytes) {
		return new String(bytes, charSet);
	}

	public static byte[] toBytes(String str) {
		return str.getBytes(charSet);
	}
}
