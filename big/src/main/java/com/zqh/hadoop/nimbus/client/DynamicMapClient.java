package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;

import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;

public class DynamicMapClient implements NotificationListener, Iterable<Entry<String, String>> {

	private static final Logger LOG = Logger.getLogger(DynamicMapClient.class);

	private HashMap<Integer, DynamicMapCacheletConnection> list = new HashMap<Integer, DynamicMapCacheletConnection>();
	private int numServers = -1;

	private HashSet<Integer> tempConnectionMap = new HashSet<Integer>();
	private ICacheletHash cacheletHash = ICacheletHash.getInstance();
	private int replication;
	private DynamicMapCacheletConnection tempConnection;
	private Map<Integer, Map<String, String>> bufferedElements = new HashMap<Integer, Map<String, String>>();

	public DynamicMapClient(String cacheName)
			throws CacheDoesNotExistException, IOException {
		this.replication = NimbusConf.getConf().getReplicationFactor();
		String[] cachelets = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		for (int i = 0; i < cachelets.length; ++i) {
			list.put(i, new DynamicMapCacheletConnection(cacheName,
					cachelets[i]));
		}

		numServers = cachelets.length;

		for (int i = 0; i < numServers; ++i) {
			bufferedElements.put(i, new HashMap<String, String>());
		}

		// heuristic to find the tenured pool (largest heap) as seen on
		// http://www.javaspecialists.eu/archive/Issue092.html
		MemoryPoolMXBean tenuredGenPool = null;
		for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
			if (pool.getType() == MemoryType.HEAP
					&& pool.isUsageThresholdSupported()) {
				tenuredGenPool = pool;
			}
		}

		// we do something when we reached 80% of memory usage
		tenuredGenPool.setCollectionUsageThreshold((int) Math
				.floor(tenuredGenPool.getUsage().getMax() * 0.8));

		// set a listener
		MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
		NotificationEmitter emitter = (NotificationEmitter) mbean;
		emitter.addNotificationListener(this, null, null);
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		try {
			flush();
		} catch (CacheletNotConnectedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		// TODO Auto-generated method stub
		return new DynamicMapIterator();
	}

	/**
	 * Disconnects this set from all Cachelets.
	 */
	public synchronized void disconnect() {
		for (DynamicMapCacheletConnection worker : list.values()) {
			try {
				worker.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempConnectionMap = null;
	}

	public synchronized String putNow(String key, String value)
			throws CacheletNotConnectedException {

		tempConnectionMap.clear();
		cacheletHash.getCacheletsFromKey(key, tempConnectionMap, numServers,
				replication);

		String retval = null;
		for (Integer cacheletID : tempConnectionMap) {
			tempConnection = list.get(cacheletID);
			try {
				retval = tempConnection.put(key, value);
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					tempConnection.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					retval = tempConnection.put(key, value);
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return retval;
	}

	public synchronized void put(String key, String value)
			throws CacheletNotConnectedException {

		synchronized (bufferedElements) {
			// for each element in the set, add it to the mini shards
			tempConnectionMap.clear();
			cacheletHash.getCacheletsFromKey(key, tempConnectionMap,
					numServers, replication);
			for (Integer cacheletID : tempConnectionMap) {
				bufferedElements.get(cacheletID).put(key, value);
			}
		}
	}

	public synchronized String get(String key)
			throws CacheletNotConnectedException {

		tempConnectionMap.clear();
		cacheletHash.getCacheletsFromKey(key, tempConnectionMap, numServers,
				replication);

		String retval = null;
		for (Integer cacheletID : tempConnectionMap) {
			tempConnection = list.get(cacheletID);
			try {
				retval = tempConnection.get(key);
				if (retval != null) {
					return retval;
				}
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					tempConnection.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					retval = tempConnection.get(key);
					if (retval != null) {
						return retval;
					}
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return retval;
	}

	public synchronized void putAll(Map<? extends String, ? extends String> map)
			throws CacheletNotConnectedException {

		for (Entry<? extends String, ? extends String> entry : map.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}

		flush();
	}

	public synchronized void flush() throws CacheletNotConnectedException {
		synchronized (bufferedElements) {
			int numEntries = 0;
			for (Entry<Integer, Map<String, String>> entry : bufferedElements
					.entrySet()) {
				numEntries += entry.getValue().size();
			}

			LOG.info("Flushing " + numEntries + " total elements...");
			for (Entry<Integer, Map<String, String>> entry : bufferedElements
					.entrySet()) {
				tempConnection = list.get(entry.getKey());
				try {
					LOG.info("Flushing " + entry.getValue().size()
							+ " elements to " + entry.getKey());
					tempConnection.putAll(entry.getValue());
				} catch (IOException e) {
					try {
						LOG.error("Caught exception for ID " + entry.getKey()
								+ ": " + e.getMessage()
								+ ".  Attempting reconnect...");
						tempConnection.connect();
						LOG.info("Successfully reconnected to ID "
								+ entry.getKey());

						// retry now that we have reconnected
						tempConnection.putAll(entry.getValue());
					} catch (IOException e1) {
						LOG.error("Failed to reconnect.  Throwing exception:");
						e.printStackTrace();
						throw new CacheletNotConnectedException(entry.getKey(),
								e1);
					}
				}
			}

			// clear the buffered elements
			for (Entry<Integer, Map<String, String>> entry : bufferedElements
					.entrySet()) {
				entry.getValue().clear();
			}
		}
	}

	public synchronized void clear() throws CacheletNotConnectedException {
		for (Entry<Integer, DynamicMapCacheletConnection> entry : list
				.entrySet()) {
			try {
				entry.getValue().clear();
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + entry.getKey()
							+ ": " + e.getMessage()
							+ ".  Attempting reconnect...");
					entry.getValue().connect();
					LOG.info("Successfully reconnected to ID " + entry.getKey());
					entry.getValue().clear();
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(entry.getKey(), e1);
				}
			}
		}
	}

	public synchronized boolean containsKey(String key)
			throws CacheletNotConnectedException {

		tempConnectionMap.clear();
		cacheletHash.getCacheletsFromKey(key, tempConnectionMap, numServers,
				replication);

		for (Integer cacheletID : tempConnectionMap) {
			tempConnection = list.get(cacheletID);
			try {
				if (tempConnection.containsKey(key)) {
					return true;
				}
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					tempConnection.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					if (tempConnection.containsKey(key)) {
						return true;
					}
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return false;
	}

	public synchronized boolean containsValue(String value)
			throws CacheletNotConnectedException {

		for (Entry<Integer, DynamicMapCacheletConnection> entry : list
				.entrySet()) {
			try {
				if (entry.getValue().containsValue(value)) {
					return true;
				}
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + entry.getKey()
							+ ": " + e.getMessage()
							+ ".  Attempting reconnect...");
					tempConnection.connect();
					LOG.info("Successfully reconnected to ID " + entry.getKey());

					// retry now that we have reconnected
					if (tempConnection.containsValue(value)) {
						return true;
					}
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(entry.getKey(), e1);
				}
			}
		}

		return false;
	}

	public synchronized boolean isEmpty() throws CacheletNotConnectedException {
		for (Entry<Integer, DynamicMapCacheletConnection> entry : list
				.entrySet()) {
			try {
				if (!entry.getValue().isEmpty()) {
					return false;
				}
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + entry.getKey()
							+ ": " + e.getMessage()
							+ ".  Attempting reconnect...");
					entry.getValue().connect();
					LOG.info("Successfully reconnected to ID " + entry.getKey());

					if (!entry.getValue().isEmpty()) {
						return false;
					}

				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(entry.getKey(), e1);
				}
			}
		}
		return true;
	}

	public synchronized String remove(String key)
			throws CacheletNotConnectedException {
		tempConnectionMap.clear();
		cacheletHash.getCacheletsFromKey(key, tempConnectionMap, numServers,
				replication);

		String retval = null;
		for (Integer cacheletID : tempConnectionMap) {
			tempConnection = list.get(cacheletID);
			try {
				retval = tempConnection.remove(key);
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					tempConnection.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					retval = tempConnection.remove(key);
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return retval;
	}

	public synchronized int size() throws CacheletNotConnectedException {
		int size = 0;
		for (Entry<Integer, DynamicMapCacheletConnection> entry : list
				.entrySet()) {
			try {
				size += entry.getValue().size();
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + entry.getKey()
							+ ": " + e.getMessage()
							+ ".  Attempting reconnect...");
					entry.getValue().connect();
					LOG.info("Successfully reconnected to ID " + entry.getKey());

					size += entry.getValue().size();

				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(entry.getKey(), e1);
				}
			}
		}

		return size;
	}
	
	private class DynamicMapIterator implements Iterator<Entry<String, String>> {

		private Iterator<DynamicMapCacheletConnection> cacheletsIter = null;
		private Iterator<Entry<String, String>> iter = null;

		public DynamicMapIterator() {
			cacheletsIter = list.values().iterator();
			if (cacheletsIter.hasNext()) {
				iter = cacheletsIter.next().iterator();
			}
		}

		@Override
		public boolean hasNext() {
			return cacheletsIter.hasNext() || iter.hasNext();
		}

		@Override
		public Entry<String, String> next() {
			do {
				if (iter.hasNext()) {
					return iter.next();
				} else if (cacheletsIter.hasNext()) {
					iter = cacheletsIter.next().iterator();
				} else {
					break;
				}
			} while (true);

			return null;
		}

		@Override
		public void remove() {
			throw new RuntimeException(
					"NimbusMapCacheletIterator::remove is unsupported");
		}
	}
}