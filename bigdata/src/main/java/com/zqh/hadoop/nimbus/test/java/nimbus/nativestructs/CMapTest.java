package com.zqh.hadoop.nimbus.test.java.nimbus.nativestructs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.nativestructs.CMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CMapTest {

	private CMap map = null;

	@Before
	public void mapup() {
		map = CMap.getInstance();
	}

	@Test
	public void testContainsKey() {
		Assert.assertFalse(map.containsKey("TEST"));
		map.put("TEST", "VALUE");
		Assert.assertTrue(map.containsKey("TEST"));
	}

	@Test
	public void testContainsValue() {
		Assert.assertFalse(map.containsValue("VALUE"));
		map.put("TEST", "VALUE");
		Assert.assertTrue(map.containsValue("VALUE"));
	}

	@Test
	public void testPut() {
		Assert.assertNull(map.put("TEST", "VALUE"));
		Assert.assertEquals("VALUE", map.put("TEST", "UPDATED_VALUE"));

		Assert.assertEquals(1, map.size());
		Assert.assertEquals("UPDATED_VALUE", map.get("TEST"));
	}

	@Test
	public void testPutAll() {
		Assert.assertFalse(map.containsKey("TEST"));
		Assert.assertFalse(map.containsKey("TEST2"));

		Map<String, String> values = new HashMap<String, String>();
		values.put("TEST", "VALUE");
		values.put("TEST2", "VALUE2");
		map.putAll(values);

		Assert.assertEquals("VALUE", map.get("TEST"));
		Assert.assertEquals("VALUE2", map.get("TEST2"));
	}

	@Test
	public void testRemove() {
		Assert.assertFalse(map.containsKey("TEST"));
		map.put("TEST", "VALUE");
		Assert.assertEquals("VALUE", map.get("TEST"));
		Assert.assertEquals("VALUE", map.remove("TEST"));
		Assert.assertFalse(map.containsKey("TEST"));
	}

	@Test
	public void testGet() {
		Assert.assertFalse(map.containsKey("TEST"));
		map.put("TEST", "VALUE");
		Assert.assertTrue(map.containsKey("TEST"));
		Assert.assertEquals("VALUE", map.get("TEST"));
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, map.size());
		map.put("TEST", "VALUE");
		Assert.assertEquals(1, map.size());
		map.put("TEST", "UPDATED_VALUE");
		Assert.assertEquals(1, map.size());
	}

	@Test
	public void testClear() {
		Assert.assertEquals(0, map.size());
		map.put("TEST", "VALUE");
		Assert.assertEquals(1, map.size());
		map.clear();
		Assert.assertEquals(0, map.size());
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(map.isEmpty());
		map.put("TEST", "VALUE");
		Assert.assertFalse(map.isEmpty());
	}

	@Test
	public void testIterator() {
		Assert.assertFalse(map.containsKey("TEST"));
		Assert.assertFalse(map.containsKey("TEST2"));

		map.put("TEST", "VALUE");
		map.put("TEST2", "VALUE2");

		HashMap<String, String> values = new HashMap<String, String>();
		values.put("TEST", "VALUE");
		values.put("TEST2", "VALUE2");
		int num = 0;
		for (Entry<String, String> entry : map) {
			++num;
			Assert.assertEquals(values.get(entry.getKey()), entry.getValue());
		}

		Assert.assertEquals(values.size(), num);
	}

	@Test
	public void testMultipleIterators() {
		Assert.assertFalse(map.containsKey("TEST"));
		Assert.assertFalse(map.containsKey("TEST2"));
		Assert.assertFalse(map.containsKey("TEST3"));
		Assert.assertFalse(map.containsKey("TEST4"));

		map.put("TEST", "VALUE");
		map.put("TEST2", "VALUE2");
		map.put("TEST3", "VALUE3");
		map.put("TEST4", "VALUE4");

		Iterator<Entry<String, String>> iter1 = map.iterator();

		Assert.assertEquals(map.get("TEST"), iter1.next().getValue());
		Assert.assertEquals(map.get("TEST2"), iter1.next().getValue());

		Iterator<Entry<String, String>> iter2 = map.iterator();

		Assert.assertEquals(map.get("TEST"), iter2.next().getValue());
		Assert.assertEquals(map.get("TEST2"), iter2.next().getValue());

		Assert.assertEquals(map.get("TEST3"), iter1.next().getValue());
		Assert.assertEquals(map.get("TEST4"), iter1.next().getValue());

		Assert.assertEquals(map.get("TEST3"), iter2.next().getValue());
		Assert.assertEquals(map.get("TEST4"), iter2.next().getValue());

		Assert.assertNull(iter1.next());
		Assert.assertNull(iter2.next());
	}

	/*
	 * @Test public void testMultipleIteratorsSeparateSets() {
	 * Assert.assertFalse(map.containsKey("TEST"));
	 * Assert.assertFalse(map.containsKey("TEST2"));
	 * Assert.assertFalse(map.containsKey("TEST3"));
	 * Assert.assertFalse(map.containsKey("TEST4"));
	 * 
	 * map.put("TEST", "VALUE"); map.put("TEST2", "VALUE2"); map.put("TEST3",
	 * "VALUE3"); map.put("TEST4", "VALUE4");
	 * 
	 * CMap map2 = new CMap(); map2.put("TEST5", "VALUE5"); map2.put("TEST6",
	 * "VALUE6"); map2.put("TEST7", "VALUE7"); map2.put("TEST8", "VALUE8");
	 * 
	 * Iterator<Entry<String, String>> iter1 = map.iterator();
	 * 
	 * Assert.assertEquals("TEST1", iter1.next()); Assert.assertEquals("TEST2",
	 * iter1.next());
	 * 
	 * Iterator<Entry<String, String>> iter2 = map.iterator();
	 * 
	 * Assert.assertEquals("TEST1", iter2.next()); Assert.assertEquals("TEST2",
	 * iter2.next());
	 * 
	 * Iterator<Entry<String, String>> iter3 = map2.iterator();
	 * 
	 * Assert.assertEquals("TEST5", iter3.next()); Assert.assertEquals("TEST6",
	 * iter3.next());
	 * 
	 * Assert.assertEquals("TEST3", iter1.next()); Assert.assertEquals("TEST4",
	 * iter1.next());
	 * 
	 * Iterator<Entry<String, String>> iter4 = map2.iterator();
	 * 
	 * Assert.assertEquals("TEST5", iter4.next()); Assert.assertEquals("TEST6",
	 * iter4.next());
	 * 
	 * Assert.assertEquals("TEST3", iter2.next()); Assert.assertEquals("TEST4",
	 * iter2.next());
	 * 
	 * Assert.assertEquals("TEST7", iter3.next()); Assert.assertEquals("TEST7",
	 * iter4.next()); Assert.assertEquals("TEST8", iter3.next());
	 * Assert.assertEquals("TEST8", iter4.next());
	 * 
	 * Assert.assertNull(iter1.next()); Assert.assertNull(iter2.next());
	 * Assert.assertNull(iter3.next()); Assert.assertNull(iter4.next()); }
	 */
	@After
	public void cleanup() {
		map.clear();
	}
}
