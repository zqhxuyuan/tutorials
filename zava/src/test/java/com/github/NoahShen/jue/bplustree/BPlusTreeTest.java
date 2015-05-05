package com.github.NoahShen.jue.bplustree;

import org.junit.Test;

import com.github.NoahShen.jue.KeyTreeCallBack;


public class BPlusTreeTest {	
	
	@Test
	public void testPutCallBackTest() {
		CopyOnWriteBPlusTree<Integer, String> tree = new CopyOnWriteBPlusTree<Integer, String>(2);
		TreeCallBack<Integer, String> callBack = new TreeCallBack<Integer, String>() {

			@Override
			public void nodeUpdated(BNode<Integer, String> node) {
				System.out.println("nodeUpdated:" + node.toString());
			}

			@Override
			public void nodeSplited(BNode<Integer, String> leftNode,
					BNode<Integer, String> rightNode) {
				System.out.println("nodeSplited:\n" +
									"\tleftNode:" + leftNode.toString() +
									"\n\trightNode:" + rightNode.toString());
			}

			@Override
			public void nodeMerge(BNode<Integer, String> mergedNode) {
				System.out.println("nodeMerge:" + mergedNode.toString());
			}

			@Override
			public void nodeNotChanged(BNode<Integer, String> node) {
				System.out.println("nodeNotChanged:" + node.toString());
			}

			@Override
			public void rootChanged(BNode<Integer, String> rootNode) {
				System.out.println("rootChanged:" + rootNode);
			}

			@Override
			public void nodeMoved(BNode<Integer, String> leftNode,
					BNode<Integer, String> rightNode) {
				System.out.println("nodeMove:\n" +
									"\tleftNode:" + leftNode.toString() +
									"\n\trightNode:" + rightNode.toString());
			}
		};
		
		for (int i = 1; i <= 20; ++i) {
			System.out.println(">>>>put " + i + ":'" + i + "'");
			tree.put(i, i + "", callBack);
		}
		System.out.println(tree);
	}
	
	@Test
	public void testTraverseAllNodeCallBack() {
		String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		CopyOnWriteBPlusTree<String, Long> tree = new CopyOnWriteBPlusTree<String, Long>(2);
		for (int i = 0; i < s.length(); ++i) {
			tree.put(s.substring(i, i + 1), new Long(i));
		}
		
		KeyTreeCallBack callBack = new KeyTreeCallBack(0);
		tree.traverseAllNodes(callBack);
		System.out.println(tree);
		System.out.println(callBack.getBytes().length);
	}
	
	@Test
	public void testTraverseLeaf() {
		CopyOnWriteBPlusTree<Integer, Integer> tree = new CopyOnWriteBPlusTree<Integer, Integer>(2);
		for (int i = 0; i < 20; ++i) {
			tree.put(i, i);
		}
		
		System.out.println("=======Order traversal");
		BNode<Integer, Integer> firstNode = tree.getFirstLeafNode();
		do {
			System.out.println(firstNode);
			firstNode = firstNode.getNextNode();
		} while (firstNode != null);
		
		System.out.println("=======Reverse traversal");
		BNode<Integer, Integer> lastNode = tree.getLastLeafNode();
		do {
			System.out.println(lastNode);
			lastNode = lastNode.getPrevNode();
		} while (lastNode != null);
	}
	
}

