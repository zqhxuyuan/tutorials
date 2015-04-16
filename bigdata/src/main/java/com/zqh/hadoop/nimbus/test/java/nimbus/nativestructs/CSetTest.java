package com.zqh.hadoop.nimbus.test.java.nimbus.nativestructs;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.zqh.hadoop.nimbus.nativestructs.CSet;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CSetTest {

	private CSet set = null;

	@Before
	public void setup() {
		set = new CSet();
	}

	@Test
	public void testContains() {
		try {
		Assert.assertFalse(set.contains("TEST"));
		set.add("TEST");
		Assert.assertTrue(set.contains("TEST"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testContainsAll() {
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.contains("TEST2"));

		set.add("TEST");
		set.add("TEST2");

		Set<String> values = new HashSet<String>();
		values.add("TEST");
		values.add("TEST2");
		Assert.assertTrue(set.containsAll(values));
	}

	@Test
	public void testAdd() {
		Assert.assertTrue(set.add("TEST"));
		Assert.assertTrue(set.contains("TEST"));
	}

	@Test
	public void testAddAll() {
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.contains("TEST2"));

		Set<String> values = new HashSet<String>();
		values.add("TEST");
		values.add("TEST2");

		set.addAll(values);
		Assert.assertTrue(set.contains("TEST"));
		Assert.assertTrue(set.contains("TEST2"));
		Assert.assertTrue(set.containsAll(values));
	}

	@Test
	public void testRemove() {
		Assert.assertFalse(set.contains("TEST"));
		set.add("TEST");
		Assert.assertTrue(set.contains("TEST"));
		Assert.assertTrue(set.remove("TEST"));
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.remove("TEST"));
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, set.size());
		set.add("TEST");
		Assert.assertEquals(1, set.size());
		set.add("TEST");
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testClear() {
		Assert.assertEquals(0, set.size());
		set.add("TEST");
		Assert.assertEquals(1, set.size());
		set.clear();
		Assert.assertEquals(0, set.size());
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(set.isEmpty());
		set.add("TEST");
		Assert.assertFalse(set.isEmpty());
	}

	@Test
	public void testIterator() {
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.contains("TEST2"));

		set.add("TEST");
		set.add("TEST2");

		Set<String> values = new HashSet<String>();
		values.add("TEST");
		values.add("TEST2");
		int num = 0;
		for (String s : set) {
			++num;
			Assert.assertTrue(values.contains(s));
		}

		Assert.assertEquals(values.size(), num);
	}

	@Test
	public void testMultipleIterators() {
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.contains("TEST2"));

		set.add("TEST");
		set.add("TEST2");
		set.add("TEST3");
		set.add("TEST4");

		Iterator<String> iter1 = set.iterator();

		Assert.assertEquals("TEST", iter1.next());
		Assert.assertEquals("TEST2", iter1.next());

		Iterator<String> iter2 = set.iterator();

		Assert.assertEquals("TEST", iter2.next());
		Assert.assertEquals("TEST2", iter2.next());

		Assert.assertEquals("TEST3", iter1.next());
		Assert.assertEquals("TEST4", iter1.next());

		Assert.assertEquals("TEST3", iter2.next());
		Assert.assertEquals("TEST4", iter2.next());

		Assert.assertNull(iter1.next());
		Assert.assertNull(iter2.next());
	}
	
	@Test
	public void testMultipleIteratorsSeparateSets() {
		Assert.assertFalse(set.contains("TEST"));
		Assert.assertFalse(set.contains("TEST2"));

		set.add("TEST1");
		set.add("TEST2");
		set.add("TEST3");
		set.add("TEST4");
		
		CSet set2 = new CSet();
		set2.add("TEST5");
		set2.add("TEST6");
		set2.add("TEST7");
		set2.add("TEST8");
		
		Iterator<String> iter1 = set.iterator();

		Assert.assertEquals("TEST1", iter1.next());
		Assert.assertEquals("TEST2", iter1.next());

		Iterator<String> iter2 = set.iterator();

		Assert.assertEquals("TEST1", iter2.next());
		Assert.assertEquals("TEST2", iter2.next());

		Iterator<String> iter3 = set2.iterator();

		Assert.assertEquals("TEST5", iter3.next());
		Assert.assertEquals("TEST6", iter3.next());
		
		Assert.assertEquals("TEST3", iter1.next());
		Assert.assertEquals("TEST4", iter1.next());

		Iterator<String> iter4 = set2.iterator();

		Assert.assertEquals("TEST5", iter4.next());
		Assert.assertEquals("TEST6", iter4.next());
		
		Assert.assertEquals("TEST3", iter2.next());
		Assert.assertEquals("TEST4", iter2.next());

		Assert.assertEquals("TEST7", iter3.next());
		Assert.assertEquals("TEST7", iter4.next());
		Assert.assertEquals("TEST8", iter3.next());
		Assert.assertEquals("TEST8", iter4.next());
		
		Assert.assertNull(iter1.next());
		Assert.assertNull(iter2.next());
		Assert.assertNull(iter3.next());
		Assert.assertNull(iter4.next());
	}

	@After
	public void cleanup() {
		if (set != null) {
			set.deleteCSet();
		}
	}
}
