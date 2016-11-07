package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/**
 * This class is a JNI wrapper for the C++ std::list and is used by Cachelets to
 * store the actual members of a list. While there is some minor overhead using
 * JNI, it removes the JVM memory requirements issues. <br>
 * <br>
 * Note that not all methods are supported due to interop issues. <br>
 * <br>
 * The CList is a Singleton object, due to there being no distinction of the
 * std::list. Multiple instances of a CList would all communicate with the same
 * std::list.
 */
public class CList implements List<String> {

	private static final Logger LOG = Logger.getLogger(CList.class);
	private static CList s_instance = null;

	static {
		LOG.info("Loading native libraries from: "
				+ System.getProperty("java.library.path"));
		System.loadLibrary("NativeNimbus");
	}

	protected class CListIterator implements Iterator<String> {

		private int index = 0;

		private final CList list = CList.getInstance();

		@Override
		public boolean hasNext() {
			return index < list.c_size();
		}

		@Override
		public String next() {
			if (hasNext()) {
				return list.get(index++);
			} else {
				return null;
			}
		}

		@Override
		public void remove() {
			list.remove(--index);
			index = Math.max(index, 0);
		}
	}

	public static CList getInstance() {
		if (s_instance == null) {
			s_instance = new CList();
		}
		return s_instance;
	}

	/**
	 * Initializes a new instance of the CSet
	 */
	private CList() {
	}

	@Override
	public boolean add(String value) {
		c_add(value);
		return true;
	}

	private native void c_add(String value);

	@Override
	public void add(int index, String value) {

		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: " + index + "\t Size: "
					+ size());
		}

		c_add(index, value);
	}

	private native void c_add(int index, String value);

	@Override
	public boolean addAll(Collection<? extends String> values) {
		for (String s : values) {
			c_add(s);
		}

		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends String> values) {
		if (index < 0 || index > size()) {
			throw new IndexOutOfBoundsException("Index: " + index + "\t Size: "
					+ size());
		}

		int i = index;
		for (String s : values) {
			c_add(i, s);
			++i;
		}

		return true;
	}

	@Override
	public void clear() {
		c_clear();
	}

	private native void c_clear();

	@Override
	public boolean contains(Object value) {
		return c_contains(value);
	}

	private native boolean c_contains(Object value);

	@Override
	public boolean containsAll(Collection<?> values) {
		boolean retval = true;

		for (Object o : values) {
			if (!c_contains(o)) {
				retval = false;
				break;
			}
		}

		return retval;
	}

	@Override
	public String get(int index) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index: " + index + "\t Size: "
					+ size());
		}

		return c_get(index);
	}

	private native String c_get(int index);

	@Override
	public int indexOf(Object obj) {
		return c_indexOf(obj);
	}

	private native int c_indexOf(Object obj);

	@Override
	public boolean isEmpty() {
		return c_isEmpty();
	}

	private native boolean c_isEmpty();

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	public Iterator<String> iterator() {
		return new CListIterator();
	}

	@Override
	public int lastIndexOf(Object obj) {
		return c_lastIndexOf(obj);
	}

	private native int c_lastIndexOf(Object obj);

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public ListIterator<String> listIterator() {
		// TODO
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public ListIterator<String> listIterator(int arg0) {
		// TODO
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public boolean remove(Object obj) {
		return c_remove(obj);
	}

	private native boolean c_remove(Object obj);

	@Override
	public String remove(int index) {
		return c_remove(index);
	}

	private native String c_remove(int index);

	@Override
	public boolean removeAll(Collection<?> objects) {

		boolean retval = false;
		Iterator<String> iter = iterator();
		while (iter.hasNext()) {
			if (objects.contains(iter.next())) {
				iter.remove();
				retval = true;
			}
		}

		return retval;
	}

	@Override
	public boolean retainAll(Collection<?> objects) {

		boolean retval = false;
		Iterator<String> iter = iterator();
		while (iter.hasNext()) {
			if (!objects.contains(iter.next())) {
				iter.remove();
				retval = true;
			}
		}

		return retval;
	}

	@Override
	public String set(int index, String value) {
		if (index < 0 || index >= size()) {
			throw new IndexOutOfBoundsException("Index: " + index + "\t Size: "
					+ size());
		}
		
		return c_set(index, value);
	}

	private native String c_set(int index, String value);

	@Override
	public int size() {
		return c_size();
	}

	private native int c_size();

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public List<String> subList(int index1, int index2) {
		// TODO
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public Object[] toArray() {
		// TODO
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO
		throw new RuntimeException("Not yet implemented");
	}
}
