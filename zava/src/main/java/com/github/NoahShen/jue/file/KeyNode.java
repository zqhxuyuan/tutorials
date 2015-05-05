/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.github.NoahShen.jue.JueConstant;

/**
 * Key的B+树的节点
 * @author noah
 */
public class KeyNode implements ADrop {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1155519276926658836L;
	
	/**
	 * 是否是叶子节点
	 */
	private byte leaf;
	
	/**
	 * 主键
	 */
	private byte[][] keys;
	
	/**
	 * 子节点或者键的地址
	 */
	private long[] childOrKeyPos;

	public KeyNode(byte leaf, byte[][] keys, long[] childOrKeyPos) {
		super();
		this.leaf = leaf;
		this.keys = keys;
		this.childOrKeyPos = childOrKeyPos;
	}

	public byte getLeaf() {
		return leaf;
	}

	public byte[][] getKeys() {
		return keys;
	}

	public long[] getChildOrKeyPos() {
		return childOrKeyPos;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		try {
			for (int i = 0; i < keys.length; ++i) {
				s.append(new String(keys[i], JueConstant.CHARSET));
				s.append(",");
			}
		} catch (UnsupportedEncodingException e) {
		}
		s.append("]");
		return "KeyNode [leaf=" + leaf + ", keys=" + s.toString()
				+ ", childOrKeyPos=" + Arrays.toString(childOrKeyPos) + "]";
	}

	
}
