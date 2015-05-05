/**
 * 
 */
package com.github.NoahShen.jue;

import com.github.NoahShen.jue.bplustree.BNode;
import com.github.NoahShen.jue.bplustree.TraverseCallBack;
import com.github.NoahShen.jue.bplustree.TreeCallBack;
import com.github.NoahShen.jue.file.ADrop;
import com.github.NoahShen.jue.util.ByteDynamicArray;
import com.github.NoahShen.jue.util.ByteUtil;

/**
 * @author noah
 *
 */
public class RevTreeCallBack implements TreeCallBack<Integer, Long>,
										TraverseCallBack<Integer, Long>{

	/**
	 * 将要写入文件的位置
	 */
	private long writePos;
	
	/**
	 * 生成的字节数组
	 */
	private final ByteDynamicArray byteDynamicArray;
	
	/**
	 * 是否已经有更新
	 */
	private boolean updated = false;
	
	/**
	 * 创建Key的回调函数实现类
	 * @param writePos 要写入文件的位置
	 */
	public RevTreeCallBack(long writePos) {
		super();
		this.writePos = writePos;
		this.byteDynamicArray = new ByteDynamicArray();
	}
	
	@Override
	public void nodeUpdated(BNode<Integer, Long> node) {
		storeToByteArray(node);
	}

	@Override
	public void nodeSplited(BNode<Integer, Long> leftNode, BNode<Integer, Long> rightNode) {
		storeToByteArray(leftNode);
		storeToByteArray(rightNode);
	}

	@Override
	public void nodeMerge(BNode<Integer, Long> mergedNode) {
		storeToByteArray(mergedNode);
	}

	@Override
	public void nodeMoved(BNode<Integer, Long> leftNode, BNode<Integer, Long> rightNode) {
		storeToByteArray(leftNode);
		storeToByteArray(rightNode);
	}

	@Override
	public void nodeNotChanged(BNode<Integer, Long> node) {
		if (updated) {
			storeToByteArray(node);
		}
	}

	@Override
	public void rootChanged(BNode<Integer, Long> rootNode) {
		storeToByteArray(rootNode);
	}

	public byte[] getBytes() {
		return byteDynamicArray.toByteArray();
	}
	
	/**
	 * 将BNode存储到byte数组中
	 * @param node
	 */
	private void storeToByteArray(BNode<Integer, Long> node) {
		node.setPosition(writePos + byteDynamicArray.size());
		byte[] b = bNodeToBytes(node);
		byteDynamicArray.add(b);
		if (!updated) {
			updated = true;
		}
	}
	
	/**
	 * 将BNode转换为byte数组
	 * @param bNode
	 * @return
	 */
	private byte[] bNodeToBytes(BNode<Integer, Long> bNode) {
		ByteDynamicArray array = new ByteDynamicArray();
		boolean isLeaf = bNode.isLeaf();
		// 是否叶节点
		array.add(isLeaf ? ADrop.TRUE_BYTE : ADrop.FALSE_BYTE);
		BNode<Integer, Long>.InnerNode[] innerNodes = bNode.getInnerNodes();
		// 关键字的数量
		array.add(ByteUtil.int2byte(bNode.getCount()));
		for (int i = 0; i < bNode.getCount(); ++i) {
			BNode<Integer, Long>.InnerNode innerNode = innerNodes[i];
			int key = innerNode.getKey();
			// 版本号
			array.add(ByteUtil.int2byte(key));
		}
		// 添加子树或者键记录的地址
		if (isLeaf) {
			for (int i = 0; i < bNode.getCount(); ++i) {
				BNode<Integer, Long>.InnerNode innerNode = innerNodes[i];
				long recordPos = innerNode.getValue();
				array.add(ByteUtil.long2byte(recordPos));
			}
		} else {
			BNode<Integer, Long>[] childNodes = bNode.getChildNodes();
			for (int i = 0; i < bNode.getCount() + 1; ++i) {
				BNode<Integer, Long> childNode = childNodes[i];
				long childPos = childNode.getPosition();
				array.add(ByteUtil.long2byte(childPos));
			}
		}
		return array.toByteArray();
	}

	@Override
	public void traverse(BNode<Integer, Long> node) {
		byte[] b = bNodeToBytes(node);
		long pos = byteDynamicArray.size() + writePos;
		byteDynamicArray.add(b);
		node.setPosition(pos);
	}
}
