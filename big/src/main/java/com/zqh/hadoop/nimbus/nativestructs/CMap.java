package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This class is a JNI wrapper for the C++ std::map and is used by Cachelets to
 * store the actual members of a map. While there is some minor overhead uming
 * JNI, it removes the JVM memory requirements issues. <br>
 * <br>
 * Note that not all methods are supported due to interop issues. <br>
 * <br>
 * The CMap is a Singleton object, due to there being no distinction of the
 * std::map. Multiple instances of a CMap would all communicate with the same
 * std::map.
 */
public class CMap implements Map<String, String>,
		Iterable<Entry<String, String>> {

	private static final Logger LOG = Logger.getLogger(CMap.class);
	private static CMap s_instance = null;

	static {
		LOG.info("Loading native libraries from: "
				+ System.getProperty("java.library.path"));
		System.loadLibrary("NativeNimbus");
	}

	public static CMap getInstance() {
		if (s_instance == null) {
			s_instance = new CMap();
		}
		return s_instance;
	}

	/**
	 * Initializes a new instance of the CSet
	 */
	private CMap() {
	}

	@Override
	public void clear() {
		c_clear();
	}

	private native void c_clear();

	@Override
	public boolean containsKey(Object key) {
		return c_containsKey(key);
	}

	private native boolean c_containsKey(Object key);

	@Override
	public boolean containsValue(Object value) {
		return c_containsValue(value);
	}

	private native boolean c_containsValue(Object key);

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	@Deprecated
	public Set<Entry<String, String>> entrySet() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public String get(Object key) {
		return c_get(key);
	}

	private native String c_get(Object key);

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
	@Deprecated
	public Set<String> keySet() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public String put(String key, String value) {
		return c_put(key, value);
	}

	private native String c_put(String key, String value);

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		for (Entry<? extends String, ? extends String> e : m.entrySet()) {
			c_put(e.getKey(), e.getValue());
		}
	}

	@Override
	public String remove(Object key) {
		return c_remove(key);
	}

	private native String c_remove(Object key);

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
	@Deprecated
	public Collection<String> values() {
		throw new RuntimeException("Not yet implemented");
	}

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public Iterator<Entry<String, String>> iterator() {
		return new CMapIterator(this);
	}

	private native int c_iterInit();

	private native boolean c_iterHasNext(int index);

	private native String c_iterNext(int index);

	private native String c_getIterValue(int index);

	public static class CMapIterator implements Iterator<Entry<String, String>> {

		private CMap map = null;
		private int index;
		private CMapEntry currEntry = new CMapEntry();

		public CMapIterator(CMap map) {
			this.map = map;
			index = map.c_iterInit();
		}

		@Override
		public boolean hasNext() {
			return map.c_iterHasNext(index);
		}

		@Override
		public Entry<String, String> next() {
			if (map.c_iterHasNext(index)) {
				currEntry.setKey(map.c_iterNext(index));
				currEntry.setValue(map.c_getIterValue(index));
				return currEntry;
			} else {
				return null;
			}
		}

		@Override
		public void remove() {
			throw new RuntimeException("CMapIterator::remove is not supported");
		}
	}
}
