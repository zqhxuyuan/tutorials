/**
 * 
 */
package com.github.NoahShen.jue.file;

import java.util.Arrays;

/**
 * Value值的版本树节点
 * @author noah
 *
 */
public class ValueRevNode implements ADrop {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6060521308578833028L;
	
	/**
	 * 是否是叶子节点
	 */
	private byte leaf;
	
	/**
	 * 主键(版本号)
	 */
	private int[] revisions;
	
	/**
	 * 子节点或者键的地址
	 */
	private long[] childOrKeyPos;

	public ValueRevNode(byte leaf, int[] revisions, long[] childOrKeyPos) {
		super();
		this.leaf = leaf;
		this.revisions = revisions;
		this.childOrKeyPos = childOrKeyPos;
	}

	public byte getLeaf() {
		return leaf;
	}


	public int[] getRevisions() {
		return revisions;
	}

	public long[] getChildOrKeyPos() {
		return childOrKeyPos;
	}

	@Override
	public String toString() {
		return "ValueRevNode [leaf=" + leaf + ", revisions="
				+ Arrays.toString(revisions) + ", childOrKeyPos="
				+ Arrays.toString(childOrKeyPos) + "]";
	}
	
	
}
