package com.zqh.hadoop.nimbus.test.java.nimbus.nativestructs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.zqh.hadoop.nimbus.nativestructs.CList;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CListTest {

	private CList list = null;

	@Before
	public void setup() {
		list = CList.getInstance();
	}

	@Test
	public void testAdd() {
		Assert.assertTrue(list.add("TEST"));
		Assert.assertTrue(list.contains("TEST"));
	}

	@Test
	public void testAddIndex() {
		list.add(0, "TEST");
		list.add(0, "TEST2");
		list.add(0, "TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testAddLastIndex() {
		list.add("TEST");
		list.add("TEST2");
		list.add(2, "TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddOutOfBoundsIndex() {
		list.add("TEST");
		list.add("TEST2");
		list.add(3, "TEST3");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddNegativeIndex() {
		list.add(-1, "TEST");
	}

	@Test
	public void testAddAll() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		Assert.assertTrue(list.contains("TEST"));
		Assert.assertTrue(list.contains("TEST2"));
		Assert.assertTrue(list.containsAll(values));

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testAddAllIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.add("FIRST VALUE");
		list.add("LAST VALUE");
		list.addAll(1, values);
		Assert.assertTrue(list.contains("TEST"));
		Assert.assertTrue(list.contains("TEST2"));
		Assert.assertTrue(list.containsAll(values));

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("FIRST VALUE", iter.next());
		Assert.assertEquals("TEST", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("LAST VALUE", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testAddAllLastIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.add("FIRST VALUE");
		list.add("SECOND VALUE");
		list.addAll(2, values);

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("FIRST VALUE", iter.next());
		Assert.assertEquals("SECOND VALUE", iter.next());
		Assert.assertEquals("TEST", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddAllOutOfBoundsIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.add("FIRST VALUE");
		list.add("SECOND VALUE");
		list.addAll(3, values);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testAddAllNegativeIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.add("FIRST VALUE");
		list.add("LAST VALUE");
		list.addAll(-1, values);
	}

	@Test
	public void testClear() {
		Assert.assertEquals(0, list.size());
		list.add("TEST");
		Assert.assertEquals(1, list.size());
		list.clear();
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void testContains() {
		Assert.assertFalse(list.contains("TEST"));
		list.add("TEST");
		Assert.assertTrue(list.contains("TEST"));
	}

	@Test
	public void testContainsAll() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		list.add("TEST");
		list.add("TEST2");

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");
		Assert.assertTrue(list.containsAll(values));
	}

	@Test
	public void testGet() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		Assert.assertEquals("TEST", list.get(0));
		Assert.assertEquals("TEST2", list.get(1));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetOutOfBoundsIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		list.get(2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetNegativeIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		list.get(-1);
	}

	@Test
	public void testIndexOf() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		Assert.assertEquals(0, list.indexOf("TEST"));
		Assert.assertEquals(1, list.indexOf("TEST2"));
	}

	@Test
	public void testIndexOfDoesNotExist() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));
		Assert.assertFalse(list.contains("TEST3"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		Assert.assertEquals(-1, list.indexOf("TEST3"));
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(list.isEmpty());
		list.add("TEST");
		Assert.assertFalse(list.isEmpty());
	}

	@Test
	public void testIteratorHasNext() {
		Assert.assertFalse(list.iterator().hasNext());
		list.add("TEST");
		Iterator<String> iter = list.iterator();
		Assert.assertTrue(iter.hasNext());
		Assert.assertNotNull(iter.next());
		Assert.assertFalse(iter.hasNext());
	}

	@Test
	public void testIteratorNext() {
		list.add("TEST");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testIteratorRemove() {
		list.add("TEST");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST", iter.next());
		iter.remove();
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		iter = list.iterator();
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testLastIndexOf() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");
		values.add("TEST");

		list.addAll(values);
		Assert.assertEquals(2, list.lastIndexOf("TEST"));
		Assert.assertEquals(1, list.lastIndexOf("TEST2"));
	}

	@Test
	public void testLastIndexOfDoesNotExist() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));
		Assert.assertFalse(list.contains("TEST3"));

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");

		list.addAll(values);
		Assert.assertEquals(-1, list.lastIndexOf("TEST3"));
	}

	@Test(expected = RuntimeException.class)
	public void testListIterator() {
		list.listIterator();
	}

	@Test(expected = RuntimeException.class)
	public void testListIteratorIndex() {
		list.listIterator(0);
	}

	@Test
	public void testRemove() {
		list.add("TEST");

		Assert.assertTrue(list.contains("TEST"));
		Assert.assertTrue(list.remove("TEST"));

		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.remove("TEST"));
	}

	@Test
	public void testRemoveByIndex() {
		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));

		list.add("TEST");
		list.add("TEST2");

		Assert.assertTrue(list.contains("TEST"));
		Assert.assertEquals("TEST2", list.remove(1));
		Assert.assertFalse(list.contains("TEST2"));
		Assert.assertEquals("TEST", list.remove(0));
		Assert.assertFalse(list.contains("TEST"));
	}

	@Test
	public void testRemoveAll() {
		list.add("TEST");
		list.add("TEST2");
		list.add("TEST5");

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");
		values.add("TEST3");

		list.removeAll(values);

		Assert.assertFalse(list.contains("TEST"));
		Assert.assertFalse(list.contains("TEST2"));
		Assert.assertTrue(list.contains("TEST5"));
	}

	@Test
	public void testRetainAll() {
		list.add("TEST");
		list.add("TEST2");
		list.add("TEST5");

		List<String> values = new ArrayList<String>();
		values.add("TEST");
		values.add("TEST2");
		values.add("TEST3");

		list.retainAll(values);

		Assert.assertTrue(list.contains("TEST"));
		Assert.assertTrue(list.contains("TEST2"));
		Assert.assertFalse(list.contains("TEST5"));
	}

	@Test
	public void testSetFirstIndex() {
		list.add("TEST1");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		list.set(0, "SETVALUE");

		iter = list.iterator();
		Assert.assertEquals("SETVALUE", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testSetMiddleIndex() {
		list.add("TEST1");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		list.set(1, "SETVALUE");

		iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("SETVALUE", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test
	public void testSetLastIndex() {
		list.add("TEST1");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		list.set(2, "SETVALUE");

		iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("SETVALUE", iter.next());
		Assert.assertNull(iter.next());
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetOutOfBoundsIndex() {
		list.add("TEST1");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		list.set(3, "SETVALUE");
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetNegativeIndex() {
		list.add("TEST1");
		list.add("TEST2");
		list.add("TEST3");

		Iterator<String> iter = list.iterator();
		Assert.assertEquals("TEST1", iter.next());
		Assert.assertEquals("TEST2", iter.next());
		Assert.assertEquals("TEST3", iter.next());
		Assert.assertNull(iter.next());

		list.set(-1, "SETVALUE");
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, list.size());
		list.add("TEST");
		Assert.assertEquals(1, list.size());
		list.add("TEST");
		Assert.assertEquals(2, list.size());
	}

	@Test(expected = RuntimeException.class)
	public void testSubList() {
		list.subList(0, 0);
	}

	@Test(expected = RuntimeException.class)
	public void testToArray() {
		list.toArray();
	}

	@Test(expected = RuntimeException.class)
	public void testToArrayWithParam() {
		list.toArray(new String[0]);
	}

	@After
	public void cleanup() {
		list.clear();
	}
}
