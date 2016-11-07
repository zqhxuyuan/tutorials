package com.zqh.hadoop.nimbus.test.java.nimbus.nativestructs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import com.zqh.hadoop.nimbus.nativestructs.Triple;
import com.zqh.hadoop.nimbus.nativestructs.TripleSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TripleSetTest {

	private TripleSet set = null;

	@Before
	public void setup() {
		set = new TripleSet();
	}

	@Test
	public void testAddTriple() {
		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1")));
		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2")));
		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3")));

		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1")));
		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2")));
		Assert.assertTrue(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3")));

		Assert.assertTrue(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1")));
		Assert.assertTrue(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2")));
		Assert.assertTrue(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3")));

		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1")));
		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2")));
		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3")));

		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1")));
		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2")));
		Assert.assertFalse(set.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3")));

		Assert.assertFalse(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1")));
		Assert.assertFalse(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2")));
		Assert.assertFalse(set.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3")));
	}

	@Test
	public void testAddStrings() {
		Assert.assertTrue(set.add("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertTrue(set.add("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertTrue(set.add("TEST1-1", "TEST2-1", "TEST3-3"));

		Assert.assertTrue(set.add("TEST1-1", "TEST2-2", "TEST3-1"));
		Assert.assertTrue(set.add("TEST1-1", "TEST2-2", "TEST3-2"));
		Assert.assertTrue(set.add("TEST1-1", "TEST2-2", "TEST3-3"));

		Assert.assertTrue(set.add("TEST1-2", "TEST2-2", "TEST3-1"));
		Assert.assertTrue(set.add("TEST1-2", "TEST2-2", "TEST3-2"));
		Assert.assertTrue(set.add("TEST1-2", "TEST2-2", "TEST3-3"));

		Assert.assertFalse(set.add("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertFalse(set.add("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertFalse(set.add("TEST1-1", "TEST2-1", "TEST3-3"));

		Assert.assertFalse(set.add("TEST1-1", "TEST2-2", "TEST3-1"));
		Assert.assertFalse(set.add("TEST1-1", "TEST2-2", "TEST3-2"));
		Assert.assertFalse(set.add("TEST1-1", "TEST2-2", "TEST3-3"));

		Assert.assertFalse(set.add("TEST1-2", "TEST2-2", "TEST3-1"));
		Assert.assertFalse(set.add("TEST1-2", "TEST2-2", "TEST3-2"));
		Assert.assertFalse(set.add("TEST1-2", "TEST2-2", "TEST3-3"));
	}

	@Test
	public void testAddAll() {

		List<Triple> values = new ArrayList<Triple>();
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		set.addAll(values);

		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));
	}

	@Test
	public void testFullIterator() {
		List<Triple> values = new ArrayList<Triple>();

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		set.addAll(values);

		Iterator<Triple> iter = set.iterator();

		List<Triple> valuesFromSet = new ArrayList<Triple>();

		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());

		Assert.assertFalse(iter.hasNext());
		Assert.assertNull(iter.next());

		valuesFromSet.removeAll(values);
		Assert.assertEquals(0, valuesFromSet.size());
	}

	@Test
	public void testFullIteratorEmptyLastSet() {
		List<Triple> values = new ArrayList<Triple>();

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		set.addAll(values);

		set.remove("TEST1-1", "TEST2-1", "TEST3-1");
		set.remove("TEST1-1", "TEST2-1", "TEST3-2");
		set.remove("TEST1-1", "TEST2-1", "TEST3-3");

		values.clear();

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		List<Triple> valuesFromSet = new ArrayList<Triple>();
		Iterator<Triple> iter = set.iterator();

		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());

		Assert.assertFalse(iter.hasNext());
		Assert.assertNull(iter.next());

		valuesFromSet.removeAll(values);
		Assert.assertEquals(0, valuesFromSet.size());
	}

	@Test
	public void testFullIteratorEmptyMiddleSet() {
		List<Triple> values = new ArrayList<Triple>();

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		set.addAll(values);

		set.remove("TEST1-1", "TEST2-1", "TEST3-1");
		set.remove("TEST1-1", "TEST2-1", "TEST3-2");
		set.remove("TEST1-1", "TEST2-1", "TEST3-3");
		set.remove("TEST1-1", "TEST2-2", "TEST3-1");
		set.remove("TEST1-1", "TEST2-2", "TEST3-2");
		set.remove("TEST1-1", "TEST2-2", "TEST3-3");

		values.clear();

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		List<Triple> valuesFromSet = new ArrayList<Triple>();
		Iterator<Triple> iter = set.iterator();

		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());

		Assert.assertFalse(iter.hasNext());
		Assert.assertNull(iter.next());

		valuesFromSet.removeAll(values);
		Assert.assertEquals(0, valuesFromSet.size());
	}

	@Test
	public void testOneIterator() {
		List<Triple> values = new ArrayList<Triple>();

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		set.addAll(values);

		Iterator<Triple> iter = set.iterator("TEST1-1");

		List<Triple> valuesFromSet = new ArrayList<Triple>();

		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());

		Assert.assertFalse(iter.hasNext());
		Assert.assertNull(iter.next());

		valuesFromSet.removeAll(values);
		Assert.assertEquals(0, valuesFromSet.size());
	}

	@Test
	public void testTwoIterator() {
		List<Triple> values = new ArrayList<Triple>();

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-2", "TEST3-3"));

		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-1"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-2"));
		values.add(new Triple("TEST1-2", "TEST2-2", "TEST3-3"));

		set.addAll(values);

		Iterator<Triple> iter = set.iterator("TEST1-1", "TEST2-1");

		List<Triple> valuesFromSet = new ArrayList<Triple>();

		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());
		valuesFromSet.add(iter.next());

		Assert.assertFalse(iter.hasNext());
		Assert.assertNull(iter.next());

		valuesFromSet.removeAll(values);
		Assert.assertEquals(0, valuesFromSet.size());
	}

	@Test
	public void testClear() {
		set.add("TEST1-1", "TEST2-1", "TEST3-1");
		set.add("TEST1-1", "TEST2-1", "TEST3-2");
		set.add("TEST1-1", "TEST2-1", "TEST3-3");

		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));

		set.clear();
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));
	}

	@Test
	public void testContainsTriple() {
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-2")));
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-3")));

		set.add("TEST1-1", "TEST2-1", "TEST3-1");
		set.add("TEST1-1", "TEST2-1", "TEST3-2");
		set.add("TEST1-1", "TEST2-1", "TEST3-3");

		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-2")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-3")));
	}

	@Test
	public void testContainStrings() {
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));

		set.add("TEST1-1", "TEST2-1", "TEST3-1");
		set.add("TEST1-1", "TEST2-1", "TEST3-2");
		set.add("TEST1-1", "TEST2-1", "TEST3-3");

		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));
	}

	@Test
	public void testContainsAll() {
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-2"));
		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-3"));

		List<Triple> values = new ArrayList<Triple>();
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));
		set.add("TEST1-1", "TEST2-1", "TEST3-1");
		set.add("TEST1-1", "TEST2-1", "TEST3-2");

		Assert.assertFalse(set.containsAll(values));

		set.add("TEST1-1", "TEST2-1", "TEST3-3");

		Assert.assertTrue(set.containsAll(values));
	}

	@Test
	public void testIsEmpty() {
		Assert.assertTrue(set.isEmpty());
		set.add("TEST1-1", "TEST2-1", "TEST3-3");
		Assert.assertFalse(set.isEmpty());
		set.clear();
		Assert.assertTrue(set.isEmpty());
	}

	@Test
	public void testRemoveTriple() {
		set.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));

		Assert.assertTrue(set
				.remove(new Triple("TEST1-1", "TEST2-1", "TEST3-1")));

		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
		Assert.assertFalse(set.remove(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
	}

	@Test
	public void testRemoveStrings() {
		set.add("TEST1-1", "TEST2-1", "TEST3-1");
		Assert.assertTrue(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));

		Assert.assertTrue(set.remove("TEST1-1", "TEST2-1", "TEST3-1"));

		Assert.assertFalse(set.contains("TEST1-1", "TEST2-1", "TEST3-1"));
		Assert.assertFalse(set.remove("TEST1-1", "TEST2-1", "TEST3-1"));
	}

	@Test
	public void testRemoveAll() {

		List<Triple> values = new ArrayList<Triple>();
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));
		set.addAll(values);
		set.add("TEST1-1", "TEST2-1", "TEST3-4");

		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-2")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-3")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-4")));

		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-0"));
		set.removeAll(values);

		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-0")));
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-1")));
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-2")));
		Assert.assertFalse(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-3")));
		Assert.assertTrue(set.contains(new Triple("TEST1-1", "TEST2-1",
				"TEST3-4")));
	}

	@Test
	public void testSize() {
		Assert.assertEquals(0, set.size());

		List<Triple> values = new ArrayList<Triple>();
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-1"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-2"));
		values.add(new Triple("TEST1-1", "TEST2-1", "TEST3-3"));

		set.addAll(values);
		Assert.assertEquals(3, set.size());

		set.add("TEST1-1", "TEST2-1", "TEST3-4");
		Assert.assertEquals(4, set.size());

		set.removeAll(values);
		Assert.assertEquals(1, set.size());

		set.clear();
		Assert.assertEquals(0, set.size());
	}

	@After
	public void cleanup() {
		set.clear();
	}
}
