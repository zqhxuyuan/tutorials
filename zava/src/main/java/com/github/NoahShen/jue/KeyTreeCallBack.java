/**
 * 
 */
package com.github.NoahShen.jue;

import java.io.UnsupportedEncodingException;

import com.github.NoahShen.jue.bplustree.BNode;
import com.github.NoahShen.jue.bplustree.TraverseCallBack;
import com.github.NoahShen.jue.bplustree.TreeCallBack;
import com.github.NoahShen.jue.file.ADrop;
import com.github.NoahShen.jue.util.ByteDynamicArray;
import com.github.NoahShen.jue.util.ByteUtil;

/**
 * Key的回调函数实现类，每次执行操作后，可以返回树改变需要写入磁盘的byte数组
 * @author noah
 *
 */
public class KeyTreeCallBack implements TreeCallBack<String, Long>, 
										TraverseCallBack<String, Long> {

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
	public KeyTreeCallBack(long writePos) {
		super();
		this.writePos = writePos;
		this.byteDynamicArray = new ByteDynamicArray();
	}

	@Override
	public void nodeUpdated(BNode<String, Long> node) {
		storeToByteArray(node);
	}


	@Override
	public void nodeSplited(BNode<String, Long> leftNode, BNode<String, Long> rightNode) {
		storeToByteArray(leftNode);
		storeToByteArray(rightNode);
	}

	@Override
	public void nodeMerge(BNode<String, Long> mergedNode) {
		storeToByteArray(mergedNode);
	}

	@Override
	public void nodeMoved(BNode<String, Long> leftNode, BNode<String, Long> rightNode) {
		storeToByteArray(leftNode);
		storeToByteArray(rightNode);
	}

	@Override
	public void nodeNotChanged(BNode<String, Long> node) {
		if (updated) {
			storeToByteArray(node);
		}
	}

	@Override
	public void rootChanged(BNode<String, Long> rootNode) {
		storeToByteArray(rootNode);
	}
	
	public byte[] getBytes() {
		return byteDynamicArray.toByteArray();
	}

	/**
	 * 将BNode存储到byte数组中
	 * @param node
	 */
	private void storeToByteArray(BNode<String, Long> node) {
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
	private byte[] bNodeToBytes(BNode<String, Long> bNode) {
		ByteDynamicArray array = new ByteDynamicArray();
		boolean isLeaf = bNode.isLeaf();
		// 是否叶节点
		array.add(isLeaf ? ADrop.TRUE_BYTE : ADrop.FALSE_BYTE);
		BNode<String, Long>.InnerNode[] innerNodes = bNode.getInnerNodes();
		// 关键字的数量
		array.add(ByteUtil.int2byte(bNode.getCount()));
		try {
			for (int i = 0; i < bNode.getCount(); ++i) {
				BNode<String, Long>.InnerNode innerNode = innerNodes[i];
				byte[] key = innerNode.getKey().getBytes(JueConstant.CHARSET);
				// 键的长度
				array.add(ByteUtil.int2byte(key.length));
				// 键的内容
				array.add(key);
			}
		} catch (UnsupportedEncodingException e) {
			// 默认编码不会抛出异常
		}
		// 添加子树或者键记录的地址
		if (isLeaf) {
			for (int i = 0; i < bNode.getCount(); ++i) {
				BNode<String, Long>.InnerNode innerNode = innerNodes[i];
				long recordAddr = innerNode.getValue();
				array.add(ByteUtil.long2byte(recordAddr));
			}
		} else {
			BNode<String, Long>[] childNodes = bNode.getChildNodes();
			for (int i = 0; i < bNode.getCount() + 1; ++i) {
				BNode<String, Long> childNode = childNodes[i];
				long childPos = childNode.getPosition();
				array.add(ByteUtil.long2byte(childPos));
			}
		}
		return array.toByteArray();
	}

	@Override
	public void traverse(BNode<String, Long> node) {
		byte[] b = bNodeToBytes(node);
		long pos = byteDynamicArray.size() + writePos;
		byteDynamicArray.add(b);
		node.setPosition(pos);
	}
}
