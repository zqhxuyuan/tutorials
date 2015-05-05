/**
 * 
 */
package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;

/**
 * @author noah
 *
 */
public interface BPlusTree<K extends Comparable<K>, V extends Serializable> extends Cloneable {
	
	/**
	 * 返回最小关键字数
	 * @return
	 */
	public int getM();
	
	/**
	 * 获取所有Key的数量，包括非叶节点的key
	 * @return
	 */
	public int getKeySum();
	
	/**
	 * 获取节点数
	 * @return
	 */
	public int getNodeSum();

	/**
	 * 获取树的深度
	 * @return
	 */
	public int getTreeLevel();

	/**
	 * 获取根节点
	 * @return
	 */
	public BNode<K, V> getRootNode();

	/**
	 * 获取最左的叶子节点
	 * @return
	 */
	public BNode<K, V> getFirstLeafNode();

	/**
	 * 获取最右的叶子节点
	 * @return
	 */
	public BNode<K, V> getLastLeafNode();

	/**
	 * 插入新键值
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean put(K key, V value);
	
	/**
	 * 插入新键值
	 * @param key
	 * @param value
	 * @param callback
	 * @return
	 */
	public boolean put(K key, V value, TreeCallBack<K, V> callback);
	
	/**
	 * 查找对应值
	 * @param key
	 * @return
	 */
	public V get(K key);
	
	/**
	 * 删除对应的键和值
	 * @param key
	 * @return
	 */
	public boolean delete(K key);
	
	/**
	 * 删除对应的键和值
	 * @param key
	 * @return
	 */
	public boolean delete(K key, TreeCallBack<K, V> callback);

	/**
	 * 遍历所有节点，从子节点开始遍历到父节点
	 * @param traverseCallBack
	 */
	public void traverseAllNodes(TraverseCallBack<K, V> traverseCallBack);
	
	/**
	 * 将Key-Value以数组形式返回
	 * @return
	 */
	public Entry<K, V>[] entryArray();
	
	/**
	 * Key-Value的Entry对象
	 * @author noah
	 *
	 * @param <K>
	 * @param <V>
	 */
	public interface Entry<K,V> {
		
		/**
		 * 返回键
		 * @return
		 */
		K getKey();
		
		/**
		 * 返回值
		 * @return
		 */
		V getValue();
	}
}
