package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;

public class DynamicSetClient implements Iterable<String>, NotificationListener {

	private static final Logger LOG = Logger.getLogger(DynamicSetClient.class);

	private HashMap<Integer, DynamicSetCacheletConnection> list = new HashMap<Integer, DynamicSetCacheletConnection>();
	private int numServers = -1;

	private HashSet<Integer> tempConnectionSet = new HashSet<Integer>();
	private ICacheletHash cacheletHash = ICacheletHash.getInstance();
	private int replication;
	private DynamicSetCacheletConnection contains_connect_tmp;
	private Map<Integer, Set<String>> bufferedElements = new HashMap<Integer, Set<String>>();

	public DynamicSetClient(String cacheName)
			throws CacheDoesNotExistException, IOException {
		this.replication = NimbusConf.getConf().getReplicationFactor();
		String[] cachelets = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		for (int i = 0; i < cachelets.length; ++i) {
			list.put(i, new DynamicSetCacheletConnection(cacheName,
					cachelets[i]));
		}

		numServers = cachelets.length;

		for (int i = 0; i < numServers; ++i) {
			bufferedElements.put(i, new HashSet<String>());
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

	public Iterator<String> iterator() {
		return new DynamicSetIterator();
	}

	@Override
	public void handleNotification(Notification notification, Object handback) {
		try {
			flush();
		} catch (CacheletNotConnectedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Disconnects this set from all Cachelets.
	 */
	public synchronized void disconnect() {
		for (DynamicSetCacheletConnection worker : list.values()) {
			try {
				worker.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempConnectionSet = null;
	}

	public synchronized boolean addNow(String element)
			throws CacheletNotConnectedException {

		tempConnectionSet.clear();
		cacheletHash.getCacheletsFromKey(element, tempConnectionSet,
				numServers, replication);

		boolean retval = false;
		for (Integer cacheletID : tempConnectionSet) {
			contains_connect_tmp = list.get(cacheletID);
			try {
				retval |= contains_connect_tmp.add(element);
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					contains_connect_tmp.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					retval |= contains_connect_tmp.add(element);
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return retval;
	}

	public synchronized void add(String element)
			throws CacheletNotConnectedException {

		synchronized (bufferedElements) {
			// for each element in the set, add it to the mini shards
			tempConnectionSet.clear();
			cacheletHash.getCacheletsFromKey(element, tempConnectionSet,
					numServers, replication);
			for (Integer cacheletID : tempConnectionSet) {
				bufferedElements.get(cacheletID).add(element);
			}
		}
	}

	public synchronized void addAll(Collection<? extends String> c)
			throws CacheletNotConnectedException {

		for (String s : c) {
			add(s);
		}

		flush();
	}

	public synchronized void flush() throws CacheletNotConnectedException {
		synchronized (bufferedElements) {
			int numEntries = 0;
			for (Entry<Integer, Set<String>> entry : bufferedElements
					.entrySet()) {
				numEntries += entry.getValue().size();
			}

			LOG.info("Flushing " + numEntries + " total elements...");
			for (Entry<Integer, Set<String>> entry : bufferedElements
					.entrySet()) {
				contains_connect_tmp = list.get(entry.getKey());
				try {
					LOG.info("Flushing " + entry.getValue().size()
							+ " elements to " + entry.getKey());
					contains_connect_tmp.addAll(entry.getValue());
				} catch (IOException e) {
					try {
						LOG.error("Caught exception for ID " + entry.getKey()
								+ ": " + e.getMessage()
								+ ".  Attempting reconnect...");
						contains_connect_tmp.connect();
						LOG.info("Successfully reconnected to ID "
								+ entry.getKey());

						// retry now that we have reconnected
						contains_connect_tmp.addAll(entry.getValue());
					} catch (IOException e1) {
						LOG.error("Failed to reconnect.  Throwing exception:");
						e.printStackTrace();
						throw new CacheletNotConnectedException(entry.getKey(),
								e1);
					}
				}
			}

			// clear the buffered elements
			for (Entry<Integer, Set<String>> entry : bufferedElements
					.entrySet()) {
				entry.getValue().clear();
			}
		}
	}

	public synchronized void clear() throws CacheletNotConnectedException {
		for (Entry<Integer, DynamicSetCacheletConnection> entry : list
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

	public synchronized boolean contains(String element)
			throws CacheletNotConnectedException {

		tempConnectionSet.clear();
		cacheletHash.getCacheletsFromKey(element, tempConnectionSet,
				numServers, replication);

		for (Integer cacheletID : tempConnectionSet) {
			contains_connect_tmp = list.get(cacheletID);
			try {
				if (contains_connect_tmp.contains(element)) {
					return true;
				}
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					contains_connect_tmp.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					if (contains_connect_tmp.contains(element)) {
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

	public synchronized boolean containsAll(Collection<String> c)
			throws CacheletNotConnectedException {
		for (String o : c) {
			if (!contains(o)) {
				return false;
			}
		}

		return true;
	}

	public synchronized boolean isEmpty() throws CacheletNotConnectedException {
		for (Entry<Integer, DynamicSetCacheletConnection> entry : list
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

	public synchronized boolean remove(String element)
			throws CacheletNotConnectedException {
		tempConnectionSet.clear();
		cacheletHash.getCacheletsFromKey(element, tempConnectionSet,
				numServers, replication);

		boolean retval = false;
		for (Integer cacheletID : tempConnectionSet) {
			contains_connect_tmp = list.get(cacheletID);
			try {
				retval |= contains_connect_tmp.remove(element);
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + cacheletID + ": "
							+ e.getMessage() + ".  Attempting reconnect...");
					contains_connect_tmp.connect();
					LOG.info("Successfully reconnected to ID " + cacheletID);

					// retry now that we have reconnected
					retval |= contains_connect_tmp.remove(element);
				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(cacheletID, e1);
				}
			}
		}

		return retval;
	}

	public synchronized boolean removeAll(Collection<String> c)
			throws CacheletNotConnectedException {
		boolean retval = false;
		for (String s : c) {
			retval = retval |= remove(s);
		}

		return retval;
	}

	public synchronized boolean retainAll(Collection<String> c)
			throws CacheletNotConnectedException {

		boolean retval = false;
		for (Entry<Integer, DynamicSetCacheletConnection> entry : list
				.entrySet()) {
			try {
				retval |= entry.getValue().retainAll(c);
			} catch (IOException e) {
				try {
					LOG.error("Caught exception for ID " + entry.getKey()
							+ ": " + e.getMessage()
							+ ".  Attempting reconnect...");
					entry.getValue().connect();
					LOG.info("Successfully reconnected to ID " + entry.getKey());

					retval |= entry.getValue().retainAll(c);

				} catch (IOException e1) {
					LOG.error("Failed to reconnect.  Throwing exception:");
					e.printStackTrace();
					throw new CacheletNotConnectedException(entry.getKey(), e1);
				}
			}
		}

		return retval;
	}

	public synchronized int size() throws CacheletNotConnectedException {
		int size = 0;
		for (Entry<Integer, DynamicSetCacheletConnection> entry : list
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

	private class DynamicSetIterator implements Iterator<String> {

		private Iterator<DynamicSetCacheletConnection> cacheletsIter = null;
		private Iterator<String> iter = null;

		public DynamicSetIterator() {
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
		public String next() {
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
					"NimbusSetCacheletIterator::remove is unsupported");
		}
	}
}