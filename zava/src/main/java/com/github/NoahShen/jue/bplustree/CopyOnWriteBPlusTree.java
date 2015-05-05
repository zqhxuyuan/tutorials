package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author noah
 * 实现Copy-on-write的B+树，支持并发访问
 */
public class CopyOnWriteBPlusTree<K extends Comparable<K>, V extends Serializable> implements BPlusTree<K, V>{
	/**
	* 访问锁
	*/
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * 实际操作的树对象
     */
    private volatile DefaultBPlusTree<K, V> tree;
    
    /**
     * 构造一颗空B+树
     * @param m 节点的最小关键字数量
     */
    public CopyOnWriteBPlusTree(int m) {
    	tree = new DefaultBPlusTree<K, V>(m);
    }
    
    /**
	* 返回最小关键字数
	* @return
	*/
	public int getM() {
		return tree.getM();
	}
	
	public int getKeySum() {
		return tree.getKeySum();
	}

	public int getNodeSum() {
		return tree.getNodeSum();
	}

	public int getTreeLevel() {
		return tree.getTreeLevel();
	}

	public BNode<K, V> getFirstLeafNode() {
		return tree.getFirstLeafNode();
	}

	public BNode<K, V> getLastLeafNode() {
		return tree.getLastLeafNode();
	}

	public BNode<K, V> getRootNode() {
		return tree.getRootNode();
	}

	/**
	* 插入新键值
	* @param key
	* @param value
	* @return
	*/
	public boolean put(K key, V value) {
		return put(key, value, null);
	}
	
	/**
	* 插入新键值
	* @param key
	* @param value
	* @param callback
	* @return
	*/
	public boolean put(K key, V value, TreeCallBack<K, V> callback) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			DefaultBPlusTree<K, V> newTree = createNewTree();
			// 在新的树节点上进行操作
			boolean result = newTree.put(key, value, callback);
			// 替换原先的树
			tree = newTree;
		   return result;
		} finally {
		   lock.unlock();
		}
	}

	/**
	* 复制当前树
	* @return
	*/
	private DefaultBPlusTree<K, V> createNewTree() {
		try {
			DefaultBPlusTree<K, V> newTree = tree.clone();
			return newTree;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("clone tree failed");
		}
		
	}

	/**
	* 查找对应值
	* @param key
	* @return
	*/
	public V get(K key) {
		return tree.get(key);
	}
	
	/**
	* 删除对应的键和值
	* @param key
	* @return
	*/
	public boolean delete(K key) {
		return delete(key, null);
	}
	
	/**
	* 删除对应的键和值
	* @param key
	* @return
	*/
	public boolean delete(K key, TreeCallBack<K, V> callback) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			DefaultBPlusTree<K, V> newTree = createNewTree();
			// 在新的树节点上进行操作
			boolean result = newTree.delete(key, callback);
			// 替换原先的树
			tree = newTree;
		   return result;
		} finally {
		   lock.unlock();
		}
	}
	
	/**
	 * 使用新的根节点更新树信息
	 * @param newTree
	 * @param newRootNode
	 */
	public void updateNewTree(BNode<K, V> newRootNode) {
		final ReentrantLock lock = this.lock;
		lock.lock();
		try {
			tree.updateNewTree(tree, newRootNode);
		} finally {
		   lock.unlock();
		}
	}

	@Override
	public void traverseAllNodes(TraverseCallBack<K, V> traverseCallBack) {
		tree.traverseAllNodes(traverseCallBack);
	}

	@Override
	public Entry<K, V>[] entryArray() {
		return tree.entryArray();
	}
	
	
	@Override
	public String toString() {
		return tree.toString();
	}
}
