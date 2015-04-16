package com.zqh.hadoop.nimbus.nativestructs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class CMapSet implements Map<String, CSet> {

	private static final Logger LOG = Logger.getLogger(CMapSet.class);
	private static CMapSet s_instance = null;
	
	private Map<String, CSet> myMap = new HashMap<String, CSet>();

	static {
		LOG.info("Loading native libraries from: "
				+ System.getProperty("java.library.path"));
		System.loadLibrary("NativeNimbus");
	}

	public static CMapSet getInstance() {
		if (s_instance == null) {
			s_instance = new CMapSet();
		}
		return s_instance;
	}

	/**
	 * Initializes a new instance of the CSet
	 */
	private CMapSet() {
	}

	@Override
	public void clear() {
		
		for (Entry<String, CSet> entry : myMap.entrySet()) {
			entry.getValue().deleteCSet();
		}
		
		myMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return c_containsKey((String)key);
	}

	private native boolean c_containsKey(String key);

	@Override
	public boolean containsValue(Object value) {
		return c_containsValue((String)value);
	}

	private native boolean c_containsValue(String key);

	/**
	 * <b>This method is not supported and throws a RuntimeException</b>
	 * 
	 * @throws RuntimeException
	 */
	@Override
	public Set<Entry<String, CSet>> entrySet() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public CSet get(Object key) {
		int index = c_get((String)key);
		return new CSet(index);
	}

	private native int c_get(String key);

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
	public CSet keySet() {
		throw new RuntimeException("Not yet implemented");
	}

	@Override
	public CSet put(String key, CSet value) {
		return c_put(key, value);
	}

	private native CSet c_put(String key, CSet value);

	@Override
	public void putAll(Map<? extends String, ? extends CSet> m) {
		for (Entry<? extends String, ? extends CSet> e : m.entrySet()) {
			c_put(e.getKey(), e.getValue());
		}
	}

	@Override
	public CSet remove(Object key) {
		return c_remove((String)key);
	}

	private native CSet c_remove(String key);

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
	public Collection<CSet> values() {
		throw new RuntimeException("Not yet implemented");
	}
}
