package com.github.NoahShen.jue.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 动态数组
 * @author noah
 *
 */
public class ByteDynamicArray {

	/**
	 * 数据
	 */
	private byte bytes[];

	/**
	 * 大小
	 */
	private int count;

	/**
	 * 使用默认大小创建一个数组
	 */
	public ByteDynamicArray() {
		this(32);
	}

	/**
	 * 使用指定大小创建数组
	 * 
	 * @param size
	 */
	public ByteDynamicArray(int size) {
		if (size < 0) {
			throw new IllegalArgumentException("Negative initial size: " + size);
		}
		bytes = new byte[size];
	}
	
	/**
	 * 使用字节数组创建
	 * 
	 * @param size
	 */
	public ByteDynamicArray(byte[] bytes) {
		this.bytes = bytes;
		this.count = bytes.length;
	}

	public void add(byte b) {
		add(new byte[]{b});
	}
	
	
	public void add(byte[] b) {
		add(b, 0, b.length);
	}
	
	/**
	 * 添加byte数组
	 * @param b
	 * @param off
	 * @param len
	 */
	public void add(byte b[], int off, int len) {
		if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		int newcount = count + len;
		if (newcount > bytes.length) {
			bytes = Arrays.copyOf(bytes, Math.max(bytes.length << 1, newcount));
		}
		System.arraycopy(b, off, bytes, count, len);
		count = newcount;
	}


	/**
	 * 清空数组
	 */
	public void clear() {
		count = 0;
	}

	/**
	 * 获取子数组
	 * @param offset
	 * @param len
	 * @return
	 */
	public byte[] getSubArray(int offset, int len) {
		return Arrays.copyOfRange(bytes, offset, offset + len);
	}
	
	/**
	 * 读取数据到Buffer中
	 * @param readBuffer
	 * @param position
	 * @return
	 */
	public int read(ByteBuffer readBuffer, int position) {
		int count = 0;
		for (int p = position; readBuffer.hasRemaining() &&  p < size(); ++p) {
			readBuffer.put(bytes[p]);
			++count;
		}
		return count;
	}
	
	/**
	 * 获取原始数组对象，如果在某些场景下，避免复制数组造成内存或者资源占用
	 * @return
	 */
	public byte[] getOrginalBytes() {
		return bytes;
	}
	
	/**
	 * 返回数组
	 * @return
	 */
	public byte[] toByteArray() {
		return Arrays.copyOf(bytes, count);
	}

	/**
	 * 返回大小
	 * @return
	 */
	public int size() {
		return count;
	}
}
