package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;

/**
 * 
 * @author Administrator
 *
 */
public interface TreeCallBack<K extends Comparable<K>, V extends Serializable> {
	
	/**
	* 节点更新
	* @param node
	*/
	public void nodeUpdated(BNode<K, V> node);
	
	/**
	* 节点分裂
	* @param leftNode
	* @param rightNode
	*/
	public void nodeSplited(BNode<K, V> leftNode, BNode<K, V> rightNode);
	
	/**
	* 节点合并
	* @param mergedNode
	*/
	public void nodeMerge(BNode<K, V> mergedNode);
	
	/**
	* 节点拆解转移
	* @param leftNode
	* @param rightNode
	*/
	public void nodeMoved(BNode<K, V> leftNode, BNode<K, V> rightNode);
	
	/**
	* 节点不需要进行操作，未改变
	*/
	public void nodeNotChanged(BNode<K, V> node);
	
	/**
	* 根节点改变
	* @param rootNode
	*/
	public void rootChanged(BNode<K, V> rootNode);
}
