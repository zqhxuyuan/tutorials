package com.zqh.hadoop.nimbus.server;

import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.nativestructs.CSet;

public class DynamicSetCacheletServer extends ICacheletServer implements
		Iterable<String> {

	public static final int ADD_CMD = 1;
	public static final int ADD_ALL_CMD = 2;
	public static final int CLEAR_CMD = 3;
	public static final int CONTAINS_CMD = 4;
	public static final int ISEMPTY_CMD = 5;
	public static final int ITER_CMD = 6;
	public static final int REMOVE_CMD = 7;
	public static final int RETAIN_ALL_CMD = 8;
	public static final int SIZE_CMD = 9;
	public static final int ACK_CMD = 10;

	private CSet set = new CSet();
	private static final Logger LOG = Logger
			.getLogger(DynamicSetCacheletServer.class);

	public DynamicSetCacheletServer(String cacheName, String cacheletName,
			int port, CacheType type) {
		super(cacheName, cacheletName, port, type);
	}

	@Override
	protected ICacheletWorker getNewWorker() {
		return new DynamicSetCacheletWorker(this);
	}

	@Override
	protected void startStatusThread() {
		Thread t = new Thread(new StatusThread());
		t.start();
	}

	public synchronized boolean add(String element) {
		try {
			super.getWriteAheadFile().write(ADD_CMD, element);

		} catch (IOException e) {
			boolean error = true;
			e.printStackTrace();
			while (error) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				try {
					super.newWriteAheadFile();
					super.getWriteAheadFile().write(ADD_CMD, element);
					error = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return set.add(element);
	}

	public synchronized void clear() {
		try {
			super.getWriteAheadFile().write(CLEAR_CMD);

		} catch (IOException e) {
			boolean error = true;
			e.printStackTrace();
			while (error) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				try {
					super.newWriteAheadFile();
					super.getWriteAheadFile().write(CLEAR_CMD);
					error = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		set.clear();
	}

	public synchronized boolean contains(String element) {
		return set.contains(element);
	}

	public synchronized boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<String> iterator() {
		return set.iterator();
	}

	public boolean remove(String element) {
		try {
			super.getWriteAheadFile().write(REMOVE_CMD, element);

		} catch (IOException e) {
			boolean error = true;
			e.printStackTrace();
			while (error) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					e2.printStackTrace();
				}

				try {
					super.newWriteAheadFile();
					super.getWriteAheadFile().write(REMOVE_CMD, element);
					error = false;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		return set.remove(element);
	}

	public synchronized int size() {
		return set.size();
	}

	private class StatusThread implements Runnable {

		@Override
		public void run() {
			while (true) {
				LOG.info("Set size\t" + set.size());
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}