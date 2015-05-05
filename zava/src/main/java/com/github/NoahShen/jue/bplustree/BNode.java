package com.github.NoahShen.jue.bplustree;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;


/**
 * @author noah
 *
 */
public class BNode<K extends Comparable<K>, V extends Serializable> implements Cloneable {
	
	DefaultBPlusTree<K, V> tree;
	
	/**
	* 最大键数量
	*/
	private int max;
	
	/**
	* 最小键数量
	*/
	private int min;
	
	/**
	* 当前内部键的个数
	*/
	int count;
	
	/**
	* 是否是叶节点
	*/
	private boolean leaf;
	
	/**
	* 内部节点
	*/
	private InnerNode[] innerNodes;
	
	/**
	* 子节点
	*/
	private BNode<K, V>[] childNodes;
	
	/**
	* 右兄弟节点
	*/
	BNode<K, V> nextNode;
	
	/**
	* 左兄弟节点
	*/
	BNode<K, V> prevNode;
	
	/**
	 * 节点在文件中的位置
	 */
	private transient long position;
	
	/**
	* 最小节点数
	* @param m
	*/
	@SuppressWarnings("unchecked")
	public BNode(DefaultBPlusTree<K, V> tree, int min, boolean leaf) {
		super();
		this.tree = tree;
		this.min = min;
		this.leaf = leaf;
		this.max = 2 * min;
		this.innerNodes = (InnerNode[]) Array.newInstance(InnerNode.class, max);
		this.childNodes = (BNode<K, V>[]) Array.newInstance(BNode.class, max + 1);
	}

	public BNode<K, V>[] getChildNodes() {
		return childNodes;
	}
	
	public void setChildNode(int i, BNode<K, V> childNode) {
		this.childNodes[i] = childNode;
	}

	public boolean isLeaf() {
		return leaf;
	}
	
	public boolean isFull() {
		return this.count == max;
	}
	
	public void setCount(int count) {
		this.count = count;
	}

	public boolean isNotEnough() {
		return this.count < this.min;
	}
	
	public BNode<K, V> getNextNode() {
		return nextNode;
	}

	public BNode<K, V> getPrevNode() {
		return prevNode;
	}

	public InnerNode[] getInnerNodes() {
		return Arrays.copyOf(innerNodes, this.count);
	}

	public void setInnerNode(int i, InnerNode innerNode) {
		this.innerNodes[i] = innerNode;
	}

	public int getCount() {
		return count;
	}

	/**
	* 添加关键字
	* @param key
	* @param value
	* @param callback
	* @return
	*/
	boolean put(K key, V value, TreeCallBack<K, V> callback) {
		PutReturnValue pValue = putImpl(new InnerNode(key, value), callback);
		boolean success = pValue.success;
		// 达到根节点，但是还需要向上添加键，创建新的根节点
		if (pValue.newInnerNode != null) {
			BNode<K, V> newRootNode = new BNode<K, V>(this.tree, this.min, false);
			newRootNode.innerNodes[0] = pValue.newInnerNode;
			newRootNode.count = 1;
			newRootNode.childNodes[0] = this;
			newRootNode.childNodes[1] = pValue.childNode;
			tree.setRootNode(newRootNode);
			// 更新树的高度
			tree.treeLevel++;
			// 更新树的总节点数
			tree.nodeSum++;
			// 调用回调函数
			if (callback != null) {
				callback.rootChanged(newRootNode);
			}
			success = true;
		}
		return success;
	}

	/**
	* 添加关键字的实现方法
	* @param innerNode 需要添加的内部节点
	* @param callback 
	* @return
	*/
	private PutReturnValue putImpl(InnerNode innerNode, TreeCallBack<K, V> callback) {
		// 往下遍历的子树的索引，或者插入覆盖的位置
		int i = 0;
		PutReturnValue retValue = null;
		if (isLeaf()) {// 当前是叶节点，不用再向下遍历
			SearchResult result = searchKeyInLeaf(innerNode.getKey());
			i = result.index;
			if (result.found) {// 已经存在该键，覆盖该节点的值
				i = result.index;
				this.innerNodes[i] = innerNode;
				// 调用回调函数
				if (callback != null) {
					callback.nodeUpdated(this);
				}
				PutReturnValue ret = new PutReturnValue();
				ret.success = true;
				return ret;
			}
			retValue = new PutReturnValue();
			retValue.success = true;
			retValue.newInnerNode = innerNode;
			// 更新树的键的总数
			tree.keySum++;
		} else {// 非叶节点，遍历相应的子树
			SearchResult result = searchKey(innerNode.getKey());
			i = result.index;
			BNode<K, V> bNode = this.childNodes[i];
			retValue = bNode.putImpl(innerNode, callback);
		}
		// 子树插入失败，直接返回
		if (!retValue.success) {
			return retValue;
		}
		// 获取需要插入的内部节点和子树
		InnerNode iNode = retValue.newInnerNode;
		BNode<K, V> childNode = retValue.childNode;
		// 没有需要插入的内部节点，直接返回
		if (iNode == null) {
			// 调用回调函数
			if (callback != null) {
				callback.nodeNotChanged(this);
			}
			return retValue;
		}
		// 未满，则直接插入节点
		if (!isFull()) {
			insInNode(i, iNode, childNode);
			// 调用回调函数
			if (callback != null) {
				callback.nodeUpdated(this);
			}
			PutReturnValue ret = new PutReturnValue();
			ret.success = true;
			return ret;
		}
		// 分裂节点
		PutReturnValue splitRet = splitNode(i, iNode, childNode);
		// 分裂失败，直接返回
		if (!splitRet.success) {
			return splitRet;
		}
		// 调用回调函数
		if (callback != null) {
			callback.nodeSplited(this, splitRet.childNode);
		}
		PutReturnValue returnValue = new PutReturnValue();
		returnValue.success = true;
		// 子树分裂后，上层节点需要插入的新内部节点和其右子树
		returnValue.newInnerNode = splitRet.newInnerNode;
		returnValue.childNode = splitRet.childNode;
		return returnValue;
	}
	
	/**
	* 分裂节点
	* @param i 新节点的插入位置
	* @param iNode 需要插入的新内部节点
	* @param childNode 需要插入的新内部节点对应的右子树
	* @return
	*/
	private PutReturnValue splitNode(int i, InnerNode iNode, BNode<K, V> childNode) {
		// 分裂后，上层节点需要插入的新内部节点和其右子树
		InnerNode newInnerNode = null;
		BNode<K, V> newNode = new BNode<K, V>(this.tree, this.min, this.leaf);
		// 执行分裂
		if (i > this.min) {// 插入到节点的右半部分
			if (isLeaf()) {// 叶节点
				// 将右半部分插入到新节点中
				for (int j = this.min + 1, k = 0; j < this.count; ++j, ++k) {
					// 如果遍历到新插节点的插入位置，则跳过
					if (k == i - this.min - 1) {
						++k;
					}
					newNode.innerNodes[k] = this.innerNodes[j];
					this.innerNodes[j] = null;
				}
				// 将需要插入的新内部节点插入到新的节点中
				newNode.innerNodes[i - this.min - 1] = iNode;
				// 将新节点的第一个值作为需要向上插入的内部节点，节点的值可以为空
				newInnerNode = new InnerNode(newNode.innerNodes[0].getKey(), null);
				// 更新键数量
				this.count = this.min + 1;
				newNode.count = this.min;
			} else {
				// 将右半部分及其右子树插入到新节点中
				for (int j = this.min + 1, k = 0; j < this.count; ++j, ++k) {
					// 如果遍历到新插节点的插入位置，则跳过
					if (k == i - this.min - 1) {
						++k;
					}
					newNode.innerNodes[k] = this.innerNodes[j];
					this.innerNodes[j] = null;
					newNode.childNodes[k + 1] = this.childNodes[j + 1];
					this.childNodes[j + 1] = null;
				}
				// 把节点的最右子树转移成新节点的最左子树
				newNode.childNodes[0] = this.childNodes[this.min + 1];
				this.childNodes[this.min + 1] = null;
				// 将需要插入的新内部节点和子树插入到新的节点中
				newNode.innerNodes[i - this.min - 1] = iNode;
				newNode.childNodes[i - this.min] = childNode;
				// 转移后，节点中的最右内部节点作为需要向上插入的内部节点传递给父节点，节点的值可以为空
				newInnerNode = new InnerNode(this.innerNodes[this.min].getKey(), null);
				// 移除该需要插入到父节点的内部节点
				this.innerNodes[this.min] = null;
				// 更新键数量
				this.count = this.min;
				newNode.count = this.min;
			}
		} else {//插入到节点的左半部分
			if (isLeaf()) {// 叶节点
				// 将右半部分插入到新节点中(包括索引为min的内部节点)
				for (int j = this.min, k = 0; j < this.count; ++j, ++k) {
					newNode.innerNodes[k] = this.innerNodes[j];
					this.innerNodes[j] = null;
				}
				// 将需要插入的新内部节点和子树插入到新的节点中
				insInNode(i, iNode, childNode);
				// 将新节点的第一个值作为需要向上插入的内部节点，节点的值可以为空
				newInnerNode = new InnerNode(newNode.innerNodes[0].getKey(), null);
				// 更新键数量
				this.count = this.min + 1;
				newNode.count = this.min;
			} else {
				// 将右半部分及其右子树插入到新节点中(包括索引为min的内部节点)
				for (int j = this.min, k = 0; j < this.count; ++j, ++k) {
					newNode.innerNodes[k] = this.innerNodes[j];
					this.innerNodes[j] = null;
					newNode.childNodes[k + 1] = this.childNodes[j + 1];
					this.childNodes[j + 1] = null;
				}
				// 插入的位置正好是节点的中间
				if (i == this.min) {
					// 不需要插入该键，直接传递给父节，插入到父节点中
					// this.innerNodes[i] = innerNode;
					// 把要插入子树最为新节点的子树
					newNode.childNodes[0] = childNode;
					// 直接把要插入的键和值返回给上层节点
					newInnerNode =  new InnerNode(iNode.getKey(), null);
				} else {
					// 把节点的最右子树转移成新节点的最左子树
					newNode.childNodes[0] = this.childNodes[this.min];
					this.childNodes[this.min] = null;
					// 将需要插入的新内部节点和子树插入到新的节点中
					insInNode(i, iNode, childNode);
					// 转移后，节点中的最右内部节点作为需要向上插入的内部节点传递给父节点
					newInnerNode = new InnerNode(this.innerNodes[this.min].getKey(), null);
					// 移除该需要插入到父节点的内部节点
					this.innerNodes[this.min] = null;
				}
				// 更新键数量
				this.count = this.min;
				newNode.count = this.min;
			}
		}
		// 更新树的总节点数
		tree.nodeSum++;
		// 更新兄弟指针
		if(isLeaf()) {
			newNode.nextNode = this.nextNode;
			newNode.prevNode = this;
			if (this.nextNode != null) {
				this.nextNode.prevNode = newNode;
				
			}
			this.nextNode = newNode;
			//更新最后一个叶节点
			if (tree.lastLeafNode == this) {
				tree.lastLeafNode = newNode;
			}
		}
		PutReturnValue ret = new PutReturnValue();
		ret.newInnerNode = newInnerNode;
		ret.childNode = newNode;
		ret.success = true;
		return ret;
	}
	
	/**
	* 添加关键字、值以及对应的右子节点到节点内
	* @param index 插入的位置
	* @param newInnerNode
	* @param childNode
	*/
	private void insInNode(int index, InnerNode newInnerNode, BNode<K, V> childNode) {
		// 将大于插入键的内部节点和子节点向后移一位
		int start = (this.count == this.innerNodes.length) ? this.count - 2 : this.count - 1;
		for (int i = start; i >= index; --i) {
			this.innerNodes[i + 1] = this.innerNodes[i];
			this.childNodes[i + 2] = this.childNodes[i + 1];
		}
		// 插入键和子节点
		this.innerNodes[index] = newInnerNode;
		this.childNodes[index + 1] = childNode;
		// 更新键数量
		this.count++;
	}
	
	/**
	* 查找键对应的值
	* @param key
	* @return
	*/
	public V search(K key) {
		if (this.isLeaf()) {// 叶节点，直接在本节点中查找
			SearchResult result = searchKeyInLeaf(key);
			if (result.found) {
				return this.innerNodes[result.index].getValue();
			}
		} else {// 非叶节点，查找相关的子节点
			SearchResult result = searchKey(key);
			BNode<K, V> bNode = this.childNodes[result.index];
			return bNode.search(key);
		}
		return null;
	}


	/**
	* 删除键
	* @param key
	* @param callback
	* @return
	*/
	boolean delete(K key, TreeCallBack<K, V> callback) {
		DeleteReturnValue dValue = deleteImpl(key, callback);
		boolean success = dValue.success;
		// 子节点合并导致根节点为空
		if (this.count == 0) {
			BNode<K, V> newRoot = this.childNodes[0];
			this.childNodes[0] = null;
			tree.setRootNode(newRoot);
			// 更新树的高度
			tree.treeLevel--;
			// 更新树的总节点数
			tree.nodeSum--;
			// 调用回调函数
			if (callback != null) {
				callback.rootChanged(newRoot);
			}
			success = true;
		} else {
			// 调用回调函数
			if (dValue.deleteOrUpdate) {
				if (callback != null) {
					callback.nodeUpdated(this);
				}
			} else {
				if (callback != null) {
					callback.nodeNotChanged(this);
				}
			}
		}
		return success;
	}
	
	/**
	* 删除方法的实现
	* @param key
	* @param callback 
	* @return
	*/
	
	private DeleteReturnValue deleteImpl(K key, TreeCallBack<K, V> callback) {
		int i = 0;// 往下遍历的子树的索引，即删除的位置
		DeleteReturnValue retValue = null;
		if (isLeaf()) {// 当前是叶节点，不用再向下遍历，直接查找是否存在key
			retValue = new DeleteReturnValue();
			retValue.success = true;
			// 内部节点遍历
			SearchResult result = searchKeyInLeaf(key);
			i = result.index;
			if (!result.found) {
				retValue.childNode = this;
				retValue.success = false;
				return retValue;
			}
		} else {// 非叶节点，遍历相应的子树
			SearchResult result = searchKey(key);
			i = result.index;
			BNode<K, V> bNode = this.childNodes[result.index];
			retValue = bNode.deleteImpl(key, callback);
		}
		// 子树删除失败，直接返回
		if (!retValue.success) {
			// 调用回调函数
			if (callback != null) {
				callback.nodeNotChanged(retValue.childNode);
			}
			return retValue;
		}
		DeleteReturnValue returnValue = new DeleteReturnValue();
		returnValue.childNode = this;
		// success表示下面所有的子节点是否存在删除成功的
		returnValue.success = retValue.success;
		if (isLeaf()) {// 当前节点为叶节点，删除对应的键
			delInNode(i);
			// 更新树的键的总数
			tree.keySum--;
			returnValue.success = true;
			returnValue.deleteOrUpdate = true;
			return returnValue;
		}
		// 直接子节点未删除或者更新
		if (!retValue.deleteOrUpdate) {
			// 调用回调函数
			if (callback != null) {
				callback.nodeNotChanged(retValue.childNode);
			}
			return returnValue;
		}
		//判断发生删除的子树的状态
		BNode<K, V> childNode = retValue.childNode;
		// 判断节点是否处于违规状态
		if (childNode.isNotEnough()) {// 剩余的节点小于最小值
			// 是否拆借
			boolean moved = false;
			// 键的右子树
			BNode<K, V> rightNode = null;
			// 键的左子树
			BNode<K, V> leftNode = null;
			if (i != this.count) {// 最右子节点没有右兄弟
				rightNode = this.childNodes[i + 1];
			}
			if (rightNode != null) {
				if (rightNode.count > this.min) {// 右兄弟可以拆借节点
					moveToLeftNode(i, childNode, rightNode);
					// 调用回调函数
					if (callback != null) {
						callback.nodeMoved(childNode, rightNode);
					}
					moved = true;
				}
			} else {
				if (i != 0) {// 最左子节点没有左兄弟
					leftNode = this.childNodes[i - 1];
				}
				if (leftNode != null) {
					if (leftNode.count > this.min) {// 左兄弟可以拆借节点
						moveToRightNode(i - 1, leftNode, childNode);
						// 调用回调函数
						if (callback != null) {
							callback.nodeMoved(leftNode, childNode);
						}
						moved = true;
					}
				}
			}
			if (!moved) {// 无法拆借，需要合并
				BNode<K, V> n = null; // 合并后的子节点
				if (leftNode != null) { // 与左节点合并
					mergeNode(i - 1, leftNode, childNode);
					n = leftNode;
				} else { // 与右节点合并
					mergeNode(i, childNode, rightNode);
					n = childNode;
				}
				// 调用回调函数
				if (callback != null) {
					callback.nodeMerge(n);
				}
			}
			// 删除内部节点，以及无论是拆解还是合并，都产生了更新操作
			returnValue.deleteOrUpdate = true;
		} else {
			// 是否需要更新边界值
			returnValue.deleteOrUpdate = checkBoundaryValue(i, childNode);
			if (callback != null) {
				callback.nodeUpdated(retValue.childNode);
			}
		}
		returnValue.childNode = this;
		return returnValue;
	}
	
	/**
	* 合并两个子节点
	* @param keyIndex 当前键的索引位置
	* @param leftNode
	* @param right
	*/
	private void mergeNode(int keyIndex, BNode<K, V> leftNode, BNode<K, V> rightNode) {
		if (leftNode.isLeaf() && rightNode.isLeaf()) { // 叶节点直接合并
			// 将右子树的键值左移到左子树
			for (int j = rightNode.count - 1, i = leftNode.count + rightNode.count - 1; j >= 0; --j, --i) {
				leftNode.innerNodes[i] = rightNode.innerNodes[j];
			}
			// 更新左子树的键数量
			leftNode.count += rightNode.count;
			// 更新兄弟节点
			leftNode.nextNode = rightNode.nextNode;
			if (rightNode.nextNode != null) {
				rightNode.nextNode.prevNode = leftNode;
			}
			//更新最后一个叶节点
			if (tree.lastLeafNode == rightNode) {
				tree.lastLeafNode = leftNode;
			}
		} else {
			// 将当前键下移到左子树的最右位置
			leftNode.innerNodes[leftNode.count] = this.innerNodes[keyIndex];
			// 将右子树的键和对应的左子树左移到左子树
			for (int j = rightNode.count - 1, i = leftNode.count + rightNode.count; j >= 0; --j, --i) {
				leftNode.innerNodes[i] = rightNode.innerNodes[j];
				leftNode.childNodes[i] = rightNode.childNodes[j];
			}
			// 最后将右子树的最右子树转移
			leftNode.childNodes[leftNode.count + rightNode.count + 1] = rightNode.childNodes[rightNode.count];
			// 更新左子树的键数量
			leftNode.count += (rightNode.count + 1);
		}
		// 删除键及右子树
		delInNode(keyIndex);
		// 更新树的总节点数
		tree.nodeSum--;
	}


	/**
	* 将左子树的节点移动到右子树
	* @param keyIndex 当前键的索引位置
	* @param leftNode
	* @param rightNode
	*/
	private void moveToRightNode(int keyIndex, BNode<K, V> leftNode, BNode<K, V> rightNode) {
		int moveCount = (leftNode.count - rightNode.count) / 2;
		// 判断是否是叶节点
		if (leftNode.isLeaf() && rightNode.isLeaf()) {// 叶节点只需直接转移键值，并更新父节点的边界值
			// 将右子树的键值右移moveCount个位置
			for (int r = rightNode.count - 1; r >= 0; --r) {
				rightNode.innerNodes[r + moveCount] = rightNode.innerNodes[r];
			}
			// 将左子树的键值移动到右子树
			for (int j = moveCount - 1, i = leftNode.count - 1; j >= 0; --i, --j) {
				rightNode.innerNodes[j] = leftNode.innerNodes[i];
				leftNode.innerNodes[i] = null;
			}
			// 更新父节点的边界值
			this.innerNodes[keyIndex] = new InnerNode(rightNode.innerNodes[0].getKey(), null);
		} else {
			// 将右子树的键和子树右移moveCount个位置
			for (int r = rightNode.count - 1; r >= 0; --r) {
				rightNode.innerNodes[r + moveCount] = rightNode.innerNodes[r];
				rightNode.childNodes[r + moveCount + 1] = rightNode.childNodes[r + 1];
			}
			// 右子树的最左子树也右移相应的位置
			rightNode.childNodes[moveCount] = rightNode.childNodes[0];
			// 把当前的键下移到右子树
			rightNode.innerNodes[moveCount - 1] = this.innerNodes[keyIndex];
			// 把左子树的最右子树转移成右子树的最左子树
			rightNode.childNodes[moveCount - 1] = leftNode.childNodes[leftNode.count];
			// 将左子树的键和子树移动到右子树
			int i = leftNode.count - 1;
			for (int j = moveCount - 2; j >= 0; --i, --j) {
				rightNode.innerNodes[j] = leftNode.innerNodes[i];
				rightNode.childNodes[j] = leftNode.childNodes[i];
				leftNode.innerNodes[i] = null;
				leftNode.childNodes[i] = null;
			}
			// 移动完后，把左子树的最右键提升到父节点的当前键的位置上
			this.innerNodes[keyIndex] = leftNode.innerNodes[i];
		}
		// 更新键数量
		leftNode.count -= moveCount;
		rightNode.count += moveCount;
	}

	/**
	* 将右子树的节点移动到左子树
	* @param keyIndex 当前键的索引位置
	* @param leftNode
	* @param rightNode
	*/
	private void moveToLeftNode(int keyIndex, BNode<K, V> leftNode, BNode<K, V> rightNode) {
		int moveCount = (rightNode.count - leftNode.count) / 2;
		// 判断是否是叶节点
		if (leftNode.isLeaf() && rightNode.isLeaf()) {// 叶节点只需直接转移键值，并更新父节点的边界值
			// 从右子树向左子树移动键值
			for (int j = moveCount - 1, i = leftNode.count + moveCount - 1; j >= 0; j--,i--) {
				leftNode.innerNodes[i] = rightNode.innerNodes[j];
			}
			// 把右子树中的所有键值左移 moveCount 个位置
			for (int r = moveCount; r < rightNode.count; ++r) {
				rightNode.innerNodes[r - moveCount] = rightNode.innerNodes[r];
				rightNode.innerNodes[r] = null;
			}
			// 更新父节点的边界值
			this.innerNodes[keyIndex] = new InnerNode(rightNode.innerNodes[0].getKey(), null);
		} else {
			// 把当前的键下移到它的左子树
			leftNode.innerNodes[leftNode.count] = this.innerNodes[keyIndex];
			// 把右子树的最左子树转移成左子树的最右子树
			leftNode.childNodes[leftNode.count + 1] = rightNode.childNodes[0];
			// 从右子树向左子树移动 moveCount-1 个键+子树对
			for (int j = moveCount - 2, i = leftNode.count + moveCount -1; j >= 0; j--,i--) {
				leftNode.innerNodes[i] = rightNode.innerNodes[j];
				leftNode.childNodes[i + 1] = rightNode.childNodes[j + 1];
			}
			// 转移完后，把右子树的最左键提升到当前键的位置上
			this.innerNodes[keyIndex] = rightNode.innerNodes[moveCount - 1];
			// 把右子树中的所有键值和子树左移 moveCount 个位置
			// 先将最左子树移动
			rightNode.childNodes[0] = rightNode.childNodes[moveCount];
			// 移动剩余键和子树
			for (int r = moveCount; r < rightNode.count; ++r) {
				rightNode.innerNodes[r - moveCount] = rightNode.innerNodes[r];
				rightNode.childNodes[r - moveCount + 1] = rightNode.childNodes[r + 1];
				rightNode.innerNodes[r] = null;
				rightNode.childNodes[r + 1] = null;
			}
		}
		// 更新键数量
		leftNode.count += moveCount;
		rightNode.count -= moveCount;
	}


	/**
	* 处理边界值
	* @param i 判断发生删除的子节点的位置
	* @param childNode 子节点
	* @return 是否更新了边界值
	*/
	private boolean checkBoundaryValue(int i, BNode<K, V> childNode) {
		// 判断发生删除的子树的位置
		if (i > 0) {// 在第一棵子树之后的子树发生删除，需要判断边界值
			K parentKey = this.innerNodes[i - 1].getKey();
			K chileFirstKey = childNode.innerNodes[0].getKey();
			// 判断父节点的键是否需要修改
			if (parentKey.compareTo(chileFirstKey) > 0) {// 父节点键大于子节点第一个键
				// 修改父节点的该边界值为子节点的第一个键
				this.innerNodes[i - 1] = new InnerNode(chileFirstKey, null);
				return true;
			}
		}
		return false;
	}
	
	/**
	* 删除当前节点中索引对应的键以及右子树
	* @param index
	*/
	private void delInNode(int index) {
		// 将该索引之后的键和右子树左移一个位置，实现删除
		for (int i = index; i < this.count - 1; ++i) {
			this.innerNodes[i] = this.innerNodes[i + 1];
			this.childNodes[i + 1] = this.childNodes[i + 2];
		}
		// 清空最后一个键和子树，gc
		this.innerNodes[this.count - 1] = null;
		this.childNodes[this.count] = null;
		// 更新键数量
		this.count--;
	}
	
	public long getPosition() {
		return position;
	}

	public void setPosition(long position) {
		this.position = position;
	}

	/**
	* 二分法查找，在叶子节点中查找更新位置
	* @param key
	* @return
	*/
	private SearchResult searchKeyInLeaf(K key) {
		// 起点
		int low = 0;
		// 终点
		int high = this.count - 1;
		SearchResult result = new SearchResult();
		// 中间索引
		int mid = -1;
		// 比较结果
		int cmp = 0;
		while (low <= high) {
			// 中间索引
			mid = (low + high) >>> 1;
			InnerNode midInnerNode = this.innerNodes[mid];
			K midKey = midInnerNode.getKey();
			cmp = midKey.compareTo(key);
			if (cmp < 0) {// 大于中间关键字，查找后半部分
				low = mid + 1;
			} else if (cmp > 0) {// 小于中间关键字，查找前半部分
				high = mid - 1;
			} else {// 查找到
				result.found = true;
				result.index = mid;
				return result;
			}
		}
		// 未找到
		result.found = false;
		// 小于最后一次比较的键，则当前mid为插入的位置，否则返回mid + 1
		result.index = (cmp > 0) ? mid : mid + 1;
		return result;
	}
	
	/**
	* 二分法查找，在非叶子节点中查找
	* @param key
	* @return
	*/
	private SearchResult searchKey(K key) {
		// 起点
		int low = 0;
		// 终点
		int high = this.count - 1;
		SearchResult result = new SearchResult();
		// 中间索引
		int mid = -1;
		// 比较结果
		int cmp = 0;
		while (low <= high) {
			// 中间索引
			mid = (low + high) >>> 1;
			InnerNode midInnerNode = this.innerNodes[mid];
			K midKey = midInnerNode.getKey();
			cmp = midKey.compareTo(key);
			if (cmp < 0) {// 大于中间关键字，查找后半部分
				low = mid + 1;
			} else if (cmp > 0) {// 小于中间关键字，查找前半部分
				high = mid - 1;
			} else {// 查找到
				result.found = true;
		   		result.index = mid + 1;
				return result;
			}
		}
		// 未找到
		result.found = false;
		// 小于最后一次比较的键，则当前mid为插入的位置，否则返回mid + 1
		result.index = (cmp > 0) ? mid : mid + 1;
		return result;
	}
	
	/**
	 * 复制该节点，会遍历复制子节点，但是不会更新更新对兄弟节点的引用，兄弟节点的引用为空。
	 * <br>对于内部节点K、V，因为是泛型，所以没有对他们进行复制操作。
	 */
	@Override
	public BNode<K, V> clone() throws CloneNotSupportedException {
		BNode<K, V> newNode = new BNode<K, V>(null, this.min, this.leaf);
		newNode.count = this.count;
		newNode.position = this.position;
		// 复制内部节点
		for (int i = 0; i < this.count; ++i) {
			BNode<K , V>.InnerNode oldInnerNode = this.innerNodes[i];
			newNode.innerNodes[i] = newNode.new InnerNode(oldInnerNode.getKey(), oldInnerNode.getValue());
		}
		if (!isLeaf()) {
			// 复制子节点
			for (int i = 0; i < this.count + 1; ++i) {
				BNode<K , V> oldChildNode = this.childNodes[i];
				newNode.childNodes[i] = oldChildNode.clone();
			}
		}
		return newNode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Node:[");
		for (int j = 0; j < this.count; ++j) {
			sb.append(this.innerNodes[j]).append(",");
		}
		sb.append("]");
		return sb.toString();
	}

	public void printNode(int tab, StringBuilder sb) {
		for (int i = 0; i < tab; i++) {
			sb.append("\t");
		}
		sb.append("Node:[");
		for (int j = 0; j < this.count; ++j) {
			sb.append(this.innerNodes[j]).append(",");
		}
		sb.append("]\n");
		if (!isLeaf()) {
			for (int k = 0; k < this.count; ++k) {
				this.childNodes[k].printNode(tab + 1, sb);
			}
			if (this.count != 0) {
				this.childNodes[this.count].printNode(tab + 1, sb);
			}
		}
		
	}
	
	/**
	* 查询结果类
	* @author Noah
	*
	*/
	private final class SearchResult {
		/**
		* 是否查找到相应的关键字
		*/
		public boolean found = false;
		
		/**
		* 子节点索引或者键的索引
		*/
		public int index;
	}
	
	/**
	* 内部节点
	* @author noah
	*
	*/
	public final class InnerNode implements Cloneable {

		/**
		* 关键字
		*/
		private K key;
		
		/**
		* 值
		*/
		private V value;

		public InnerNode(K key, V value) {
			super();
			this.key = key;
			this.value = value;
		}

		public V getValue() {
			return value;
		}

		public K getKey() {
			return key;
		}

		@Override
		public String toString() {
			StringBuilder s = new StringBuilder();
			s.append("{key:" + key)
				.append(", value:" + value + "}");
			return s.toString();
		}
	}
	
	/**
	* 添加过程中的返回结果类
	* @author Noah
	*
	*/
	private final class PutReturnValue {
		/**
		* 添加是否成功
		*/
		public boolean success = false;
		
		/**
		* 需要父节点添加的内部节点
		*/
		public InnerNode newInnerNode;
		
		/**
		* 需要父节点添加的内部节点对应的右子树
		*/
		public BNode<K, V> childNode;
	}
	
	/**
	* 删除过程中的返回结果类
	* @author Noah
	*
	*/
	private final class DeleteReturnValue {
		/**
		* 删除是否成功
		*/
		public boolean success = false;
		
		/**
		* 直接的子节点是否发生删除或者更新
		*/
		public boolean deleteOrUpdate = false;
		
		/**
		* 发生删除的子节点
		*/
		public BNode<K, V> childNode;
	}
}



