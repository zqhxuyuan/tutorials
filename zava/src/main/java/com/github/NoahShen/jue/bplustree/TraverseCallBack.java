/**
 * 
 */
package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;

/**
 * @author noah
 *
 */
public interface TraverseCallBack<K extends Comparable<K>, V extends Serializable> {
	
	/**
	 * 遍历节点
	 * @param node
	 */
	public void traverse(BNode<K, V> node);
}
