package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This class is a JNI wrapper for the C++ std::set and is used by Cachelets to
 * store the actual members of a set. While there is some minor overhead using
 * JNI, it removes the JVM memory requirements issues. <br>
 * <br>
 * Note that not all methods are supported due to interop issues. <br>
 * <br>
 * The CSet is a Singleton object, due to there being no distinction of the
 * std::set. Multiple instances of a CSet would all communicate with the same
 * std::set.
 */
public class CSet implements Set<String> {

	private static final Logger LOG = Logger.getLogger(CSet.class);
	private static CSet s_instance = null;
	private int si;

	static {
		LOG.info("Loading native libraries from: "
				+ System.getProperty("java.library.path"));
		System.loadLibrary("NativeNimbus");
		LOG.info("Loaded native library");
	}

	/**
	 * Initializes a new instance of the CSet
	 */
	public CSet() {
		this.si = newSet();
	}
	
	/**
	 * Creates a pointer to the CSet identified by the index
	 */
	public CSet(int index) {
		this.si = index;
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.delete(si);
		super.finalize();
	}
	
	public void deleteCSet() {
		this.delete(si);
	}
	
	private native void delete(int index);

	private native int newSet();

	@Override
	public boolean add(String e) {
		return c_add(si, e);
	}

	private native boolean c_add(int si, String e);

	@Override
	public boolean addAll(Collection<? extends String> c) {
		boolean retval = false;
		for (String s : c) {
			retval = add(s.toString()) ? true : retval;
		}

		return retval;
	}

	@Override
	public void clear() {
		c_clear(si);
	}

	private native void c_clear(int si);

	@Override
	public boolean contains(Object o) {
		return c_contains(si, o.toString());
	}

	private native boolean c_contains(int si, String o);

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object o : c) {
			if (!contains(o)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return c_isEmpty(si);
	}

	private native boolean c_isEmpty(int si);

	@Override
	public boolean remove(Object o) {
		return c_remove(si, o.toString());
	}

	private native boolean c_remove(int si, String o);

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean retval = false;
		for (Object s : c) {
			retval = remove(s.toString()) ? true : retval;
		}

		return retval;
	}

	@Override
	public int size() {
		return c_size(si);
	}

	private native int c_size(int si);

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public Iterator<String> iterator() {
		return new CSetIterator(si, this);
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public Object[] toArray() {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray(Object[] a) {
		throw new RuntimeException("Not yet implemented");
	}

	private native int c_iterInit(int si);

	private native boolean c_iterHasNext(int si, int index);

	private native String c_iterNext(int si, int index);

	public static class CSetIterator implements Iterator<String> {

		private CSet set = null;
		private int si;
		private int index;

		public CSetIterator(int si, CSet set) {
			this.si = si;
			this.set = set;
			index = set.c_iterInit(si);
		}

		@Override
		public boolean hasNext() {
			return set.c_iterHasNext(si, index);
		}

		@Override
		public String next() {
			return set.c_iterNext(si, index);
		}

		@Override
		public void remove() {
			throw new RuntimeException("CSetIterator::remove is not supported");
		}
	}
}
