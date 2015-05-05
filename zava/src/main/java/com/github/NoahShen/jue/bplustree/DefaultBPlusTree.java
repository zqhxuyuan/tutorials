package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;
import java.lang.reflect.Array;

/**
 * @author Noah
 *
 */
public class DefaultBPlusTree<K extends Comparable<K>, V extends Serializable> implements BPlusTree<K, V> {
	/**
	* 根节点
	*/
	private BNode<K, V> rootNode;
	
	/**
	* 第一个叶节点，用于以链式方式遍历所有叶节点
	*/
	private BNode<K, V> firstLeafNode;
	
	/**
	* 最后一个叶节点，用于以链式方式倒序遍历所有叶节点
	*/
	BNode<K, V> lastLeafNode;

	/**
	* 树的高度
	*/
	int treeLevel;
	
	/**
	* 非叶结点中键的总数
	*/
	int keySum;
	
	/**
	* 总节点数
	*/
	int nodeSum;
	
	/**
	* 最小关键字数
	*/
	private int m;
	
	/**
	 * 创建一个空树
	 * @param m 最小关键字数
	 */
	public DefaultBPlusTree(int m) {
		super();
		this.m = m;
		init();
	}

	/**
	* 初始化
	*/
	private void init() {
		rootNode = new BNode<K, V>(this, this.m, true);
		firstLeafNode = rootNode;
		lastLeafNode = rootNode;
		treeLevel = 1;
		nodeSum = 1;
	}

	/**
	* 设置新的根节点
	* @param rootNode
	*/
	void setRootNode(BNode<K, V> rootNode) {
		this.rootNode = rootNode;
	}

	/**
	* 返回最小关键字数
	* @return
	*/
	@Override
	public int getM() {
		return m;
	}
	
	@Override
	public int getKeySum() {
		return keySum;
	}

	@Override
	public int getNodeSum() {
		return nodeSum;
	}

	@Override
	public int getTreeLevel() {
		return treeLevel;
	}

	@Override
	public BNode<K, V> getRootNode() {
		return rootNode;
	}
	
	@Override
	public BNode<K, V> getFirstLeafNode() {
		return firstLeafNode;
	}
	
	@Override
	public BNode<K, V> getLastLeafNode() {
		return lastLeafNode;
	}

	/**
	* 插入新键值
	* @param key
	* @param value
	* @return
	*/
	@Override
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
	@Override
	public boolean put(K key, V value, TreeCallBack<K, V> callback) {
		return rootNode.put(key, value, callback);
	}
	
	/**
	* 查找对应值
	* @param key
	* @return
	*/
	@Override
	public V get(K key) {
		return rootNode.search(key);
	}
	
	/**
	* 删除对应的键和值
	* @param key
	* @return
	*/
	@Override
	public boolean delete(K key) {
		return delete(key, null);
	}
	
	/**
	* 删除对应的键和值
	* @param key
	* @return
	*/
	@Override
	public boolean delete(K key, TreeCallBack<K, V> callback) {
		return rootNode.delete(key, callback);
	}

	/**
	 * 复制该树，会遍历复制子节点。
	 * <br>对于内部节点K、V，因为是泛型，所以没有对他们进行复制操作。
	 */
	@SuppressWarnings("unchecked")
	@Override
	public DefaultBPlusTree<K, V> clone() throws CloneNotSupportedException {
		DefaultBPlusTree<K, V> newTree = (DefaultBPlusTree<K, V>) super.clone();
		newTree.rootNode = null;
		newTree.firstLeafNode = null;
		newTree.lastLeafNode = null;
		newTree.treeLevel = 0;
		newTree.keySum = 0;
		newTree.nodeSum = 0;
		
		BNode<K, V> newRootNode = this.rootNode.clone();
		updateNewTree(newTree, newRootNode);
		return newTree;
	}

	/**
	 * 使用新的根节点更新树信息
	 * @param newTree
	 * @param newRootNode
	 */
	public void updateNewTree(DefaultBPlusTree<K, V> newTree, BNode<K, V> newRootNode) {
		TraverseInfo info = getNodesInfo(newTree, newRootNode);
		newTree.rootNode = newRootNode;
		newTree.keySum = info.keySum;
		newTree.firstLeafNode = info.firstLeafNode;
		newTree.lastLeafNode = info.lastLeafNode;
		newTree.nodeSum = info.nodeSum;
		newTree.treeLevel = info.treeLevel;
		newTree.m = this.m;
	}

	/**
	 * 获取节点及其子节点的相关信息
	 * @param newTree
	 * @param newRootNode
	 * @return
	 */
	private TraverseInfo getNodesInfo(DefaultBPlusTree<K, V> newTree, BNode<K, V> newRootNode) {
		TraverseInfo info = new TraverseInfo();
		info.tree = newTree;
		traverseNode(newRootNode, info, true, true);
		return info;
	}

	/**
	 * 遍历节点
	 * @param node
	 * @param info
	 * @param isLeftNode 是否是当前高度的最左节点
	 * @param isRightNode 是否是当前高度的最右节点
	 */
	private void traverseNode(BNode<K, V> node, TraverseInfo info, boolean isLeftNode, boolean isRightNode) {
		node.tree = info.tree;
		// 更新节点数
		info.nodeSum++;
		// 更新树高度
		if (!info.isLevelCompleted) {
			info.treeLevel++;
		}
		// 处理叶节点
		if (node.isLeaf()) {
			info.keySum += node.count;
			BNode<K, V> prevLeafNode = info.prevLeafNode;
			if (prevLeafNode != null) {// 前一个叶子节点非空，更新兄弟引用
				prevLeafNode.nextNode = node;
				node.prevNode = prevLeafNode;
			}
			if (isLeftNode) {// 如果是叶子节点，并且是最左子树，那么就是第一个叶子节点
				info.firstLeafNode = node;
				info.isLevelCompleted = true;// 遍历到叶子节点，计算完成
			}
			if (isRightNode) {// 如果是叶子节点，并且是最右子树，那么就是最后一个叶子节点
				info.lastLeafNode = node;
			}
			info.prevLeafNode = node;
		} else { // 遍历子树
			BNode<K, V>[] childNodes = node.getChildNodes();
			for (int i = 0; i < node.count + 1; ++i) {
				BNode<K, V> childNode = childNodes[i];
				boolean isLeftNode2 = (i == 0) && isLeftNode;
				boolean isRightNode2 = (i == node.count) && isRightNode;
				traverseNode(childNode, info, isLeftNode2, isRightNode2);
			}
		}
		
	}
	
	@Override
	public String toString() {
		StringBuilder nodeStr = new StringBuilder();
		rootNode.printNode(0, nodeStr);
		StringBuilder sb = new StringBuilder();
		sb.append("tree:{keySum:").append(keySum)
			.append(", nodeSum:").append(nodeSum)
			.append(", treeLevel:").append(treeLevel)
			.append("}\n")
			.append(nodeStr);
		return sb.toString();
	}

	@Override
	public void traverseAllNodes(TraverseCallBack<K, V> traverseCallBack) {
		traverseAllNodesImpl(this.getRootNode(), traverseCallBack);
	}
	
	public void traverseAllNodesImpl(BNode<K, V> node, TraverseCallBack<K, V> traverseCallBack) {
		if (!node.isLeaf()) {
			BNode<K, V>[] childNodes = node.getChildNodes();
			int count = node.getCount() + 1;
			for (int i = 0; i < count; ++i) {
				BNode<K, V> childNode = childNodes[i];
				traverseAllNodesImpl(childNode, traverseCallBack);
			}
		}
		traverseCallBack.traverse(node);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Entry<K, V>[] entryArray() {
		DefaultEntry<K, V>[] array = (DefaultEntry<K, V>[]) Array.newInstance(DefaultEntry.class, getKeySum());
		BNode<K, V> node = getFirstLeafNode();
		int i = 0;
		do {
			BNode<K, V>.InnerNode[] iNodes = node.getInnerNodes();
			for (int j = 0; j < node.getCount(); ++j) {
				BNode<K, V>.InnerNode iNode = iNodes[j];
				array[i] = new DefaultEntry<K, V>(iNode.getKey(), iNode.getValue());
				++i;
			}
			node = node.getNextNode();
		} while (node != null);
		return array;
	}

	/**
	 * Key-Value的Entry对象
	 * @author noah
	 *
	 * @param <K>
	 * @param <V>
	 */
	public static class DefaultEntry<K, V> implements Entry<K, V> {

		private K key;

		private V value;

		public DefaultEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}
	}
		
	private class TraverseInfo {
		/**
		 * 
		 */
		DefaultBPlusTree<K, V> tree;
		
		/**
		 * 当前的节点数
		 */
		int nodeSum;
		
		/**
		 * 当前树的高度
		 */
		int treeLevel;
		
		/**
		 * 当前键的数量
		 */
		int keySum;
		
		/**
		 * 树的高度计算是否已经完成
		 */
		boolean isLevelCompleted;
		
		/**
		 * 前一个叶子节点
		 */
		BNode<K, V> prevLeafNode;

		/**
		 * 第一个叶子节点
		 */
		BNode<K, V> firstLeafNode;
		
		/**
		 * 第一个叶子节点
		 */
		BNode<K, V> lastLeafNode;
	}

}
