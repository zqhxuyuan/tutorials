package com.github.NoahShen.jue.bplustree;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CopyOnWriteBPlusTreeTest {
	
	private CopyOnWriteBPlusTree<Integer, String> tree;
	
	@Before
	public void setUp() throws Exception {
		tree = new CopyOnWriteBPlusTree<Integer, String>(4);
	}

	@After
	public void tearDown() throws Exception {
		tree = null;
	}
	
	/**
	 * 添加数据
	 */
	@Test
	public void testTreePut() {
		for (int i = 1; i <= 100; ++i) {
			Assert.assertTrue(tree.put(i, i + ""));
		}
	}
	
	/**
	 * 获取数据
	 */
	@Test
	public void testTreeGet() {
		for (int i = 1; i <= 100; ++i) {
			tree.put(i, i + "");
		}
		for (int i = 1; i <= 100; ++i) {
			Assert.assertEquals(String.valueOf(i), tree.get(i));
		}
	}
}
