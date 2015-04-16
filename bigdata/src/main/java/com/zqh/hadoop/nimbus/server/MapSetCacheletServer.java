package com.zqh.hadoop.nimbus.server;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.nativestructs.CSet;

public class MapSetCacheletServer extends ICacheletServer implements
		Iterable<Entry<String, CSet>> {

	private Map<String, CSet> mapSet = new HashMap<String, CSet>();
	private long size = 0;

	@Override
	protected ICacheletWorker getNewWorker() {
		return new MapSetCacheletWorker(this);
	}
	
	@Override
	protected void startStatusThread() {
	}

	public MapSetCacheletServer(String cacheName, String cacheletName,
			int port, CacheType type) {
		super(cacheName, cacheletName, port, type);
	}

	public synchronized void add(String key, String value) {
		CSet set = mapSet.get(key);

		if (set == null) {
			set = new CSet();
			mapSet.put(key, set);
		}

		if (set.add(value)) {
			++size;
		}
	}

	public synchronized void remove(String key) {
		CSet set = mapSet.remove(key);
		if (set != null) {
			size -= set.size();
			set.deleteCSet();
		}
	}

	public synchronized void remove(String key, String value) {
		CSet set = mapSet.get(key);
		if (set != null) {
			if (set.remove(value)) {
				--size;
			}
		}
	}

	public synchronized boolean contains(String key) {
		return mapSet.containsKey(key);
	}

	public synchronized boolean contains(String key, String value) {
		CSet set = mapSet.get(key);

		if (set != null) {
			return set.contains(value);
		} else {
			return false;
		}
	}

	public synchronized void clear() {

		for (Entry<String, CSet> entry : mapSet.entrySet()) {
			entry.getValue().deleteCSet();
		}
		mapSet.clear();
		size = 0;
	}

	public synchronized boolean isEmpty() {
		return mapSet.isEmpty();
	}

	public synchronized long size() {
		return size;
	}

	@Override
	public Iterator<Entry<String, CSet>> iterator() {
		return mapSet.entrySet().iterator();
	}

	public CSet get(String key) {
		return mapSet.get(key);
	}
}