package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class CTripleSet {

	private static final Logger LOG = Logger.getLogger(CSet.class);
	private static CTripleSet s_instance = null;

	static {
		LOG.info("Loading native libraries from: "
				+ System.getProperty("java.library.path"));
		System.loadLibrary("NativeNimbus");
	}

	public static CTripleSet getInstance() {
		if (s_instance == null) {
			s_instance = new CTripleSet();
		}

		return s_instance;
	}

	protected class TripleSetIteratorNoSeed implements Iterator<Triple> {

		private CTripleSet set = CTripleSet.getInstance();
		private Triple current = new Triple();
		
		public TripleSetIteratorNoSeed() {
			set.c_setiter();			
		}

		@Override
		public boolean hasNext() {
			if (set.c_iterHasNext()) {
				return true;
			} else {
				set.c_freeiter();
				return false;
			}
		}

		@Override
		public Triple next() {
			set.c_iterNext(current);
			return current;
		}

		@Override
		public void remove() {
			set.c_iterRemove();
		}
	}

	private native boolean c_iterHasNext();

	private native void c_iterNext(Triple t);

	private native void c_iterRemove();

	private native void c_setiter();

	private native void c_setiter(String s1);

	private native void c_setiter(String s1, String s2);

	/**
	 * Initializes a new instance of the CSet
	 */
	private CTripleSet() {
		c_clear();
	}

	public void freeiter() {
		c_freeiter();
	}

	public native void c_freeiter();

	public boolean add(String s1, String s2, String s3) {
		return c_add(s1, s2, s3);
	}

	public boolean add(Triple t) {
		return c_add(t.getFirst(), t.getSecond(), t.getThird());
	}

	public boolean addAll(Collection<Triple> triples) {

		boolean retval = false;
		for (Triple t : triples) {
			retval |= c_add(t.getFirst(), t.getSecond(), t.getThird());
		}
		return retval;
	}

	private native boolean c_add(String s1, String s2, String s3);

	public Iterator<Triple> iterator() {
		return null;
	}

	public Iterator<Triple> iterator(String s1) {
		return null;
	}

	public Iterator<Triple> iterator(String s1, String s2) {
		return null;
	}

	public void clear() {
		c_clear();
	}

	private native void c_clear();

	public void print() {
		c_print();
	}

	private native void c_print();

	public boolean contains(Triple t) {
		return c_contains(t.getFirst(), t.getSecond(), t.getThird());
	}

	public boolean contains(String s1, String s2, String s3) {
		return c_contains(s1, s2, s3);
	}

	private native boolean c_contains(String s1, String s2, String s3);

	public boolean containsAll(Collection<Triple> c) {
		for (Triple t : c) {
			if (!contains(t)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return c_isEmpty();
	}

	private native boolean c_isEmpty();

	public boolean remove(Triple t) {
		return c_remove(t.getFirst(), t.getSecond(), t.getThird());
	}

	public boolean remove(String s1, String s2, String s3) {
		return c_remove(s1, s2, s3);
	}

	public boolean removeAll(Collection<Triple> c) {
		boolean retval = false;
		for (Triple s : c) {
			retval = remove(s) ? true : retval;
		}

		return retval;
	}

	private native boolean c_remove(String s1, String s2, String s3);

	public int size() {
		return c_size();
	}

	private native int c_size();
}
