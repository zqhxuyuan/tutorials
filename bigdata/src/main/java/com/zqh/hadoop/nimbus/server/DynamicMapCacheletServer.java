package com.zqh.hadoop.nimbus.server;

import java.util.Iterator;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.nativestructs.CMap;
import org.apache.log4j.Logger;

public class DynamicMapCacheletServer extends ICacheletServer implements
		Iterable<Entry<String, String>> {

	public static final int PUT_CMD = 1;
	public static final int PUT_ALL_CMD = 2;
	public static final int CLEAR_CMD = 3;
	public static final int CONTAINS_KEY_CMD = 4;
	public static final int CONTAINS_VALUE_CMD = 5;
	public static final int GET_CMD = 6;
	public static final int ISEMPTY_CMD = 7;
	public static final int REMOVE_CMD = 8;
	public static final int SIZE_CMD = 9;
	public static final int ITER_CMD = 10;
	public static final int ACK_CMD = 11;
	public static final int DNE_CMD = 12;

	private CMap map = CMap.getInstance();
	private static final Logger LOG = Logger
			.getLogger(DynamicMapCacheletServer.class);

	public DynamicMapCacheletServer(String cacheName, String cacheletName,
			int port, CacheType type) {
		super(cacheName, cacheletName, port, type);
	}

	@Override
	protected ICacheletWorker getNewWorker() {
		return new DynamicMapCacheletWorker(this);
	}

	@Override
	protected void startStatusThread() {
		Thread t = new Thread(new StatusThread());
		t.start();
	}

	private class StatusThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				LOG.info("Set size\t" + map.size());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public synchronized String put(String key, String value) {
		return map.put(key, value);
	}

	public synchronized void clear() {
		map.clear();
	}

	public synchronized boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public synchronized boolean containsValue(String key) {
		return map.containsValue(key);
	}

	public synchronized String get(String key) {
		return map.get(key);
	}

	public synchronized boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return map.iterator();
	}

	public String remove(String key) {
		return map.remove(key);
	}

	public synchronized int size() {
		return map.size();
	}
}