package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.NullIterator;

import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;

public class MapSetClient implements Iterable<Entry<String, String>> {

	private static final Logger LOG = Logger.getLogger(MapSetClient.class);

	private HashMap<Integer, MapSetCacheletConnection> list = new HashMap<Integer, MapSetCacheletConnection>();
	private int numServers = -1;

	private HashSet<Integer> contains_set = new HashSet<Integer>();
	private ICacheletHash cacheletHash = ICacheletHash.getInstance();
	private int replication;
	private MapSetCacheletConnection tempConnection;

	public MapSetClient(String cacheName) throws CacheDoesNotExistException,
			IOException {

		LOG.info("Connecting to cache " + cacheName);

		this.replication = NimbusConf.getConf().getReplicationFactor();
		String[] cachelets = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		for (int i = 0; i < cachelets.length; ++i) {
			list.put(i, new MapSetCacheletConnection(cacheName, cachelets[i]));
		}

		numServers = cachelets.length;
	}

	public void add(String key, String value)
			throws CacheletNotConnectedException, IOException {
		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			tempConnection = list.get(cacheletID);
			tempConnection.add(key, value);
		}
	}

	public void remove(String key) throws CacheletNotConnectedException,
			IOException {
		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			list.get(cacheletID).remove(key);
		}
	}

	public void remove(String key, String value)
			throws CacheletNotConnectedException, IOException {
		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			list.get(cacheletID).remove(key, value);
		}
	}

	public boolean contains(String key) throws CacheletNotConnectedException,
			IOException {

		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			if (list.get(cacheletID).contains(key)) {
				return true;
			}
		}

		return false;
	}

	public boolean contains(String key, String value)
			throws CacheletNotConnectedException, IOException {

		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			if (list.get(cacheletID).contains(key, value)) {
				return true;
			}
		}

		return false;
	}

	public boolean containsAll(Collection<Entry<String, String>> c)
			throws IOException {
		for (Entry<String, String> o : c) {
			if (!contains(o.getKey(), o.getValue())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determines whether or not this Cache is empty.
	 * 
	 * @return True if the Cache is empty, false otherwise.
	 * @throws IOException
	 */
	public boolean isEmpty() throws IOException {
		for (MapSetCacheletConnection mapSet : list.values()) {
			if (!mapSet.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	public void clear() throws CacheletNotConnectedException, IOException {
		for (MapSetCacheletConnection mapSet : list.values()) {
			mapSet.clear();
		}
	}

	/**
	 * Disconnects this set from all Cachelets.
	 */
	public void disconnect() {
		for (MapSetCacheletConnection worker : list.values()) {
			try {
				worker.disconnect();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		tempConnection = null;
	}

	public int size() throws IOException {

		int size = 0;
		for (MapSetCacheletConnection worker : list.values()) {
			size += worker.size();
		}

		return size;
	}

	@Override
	public Iterator<Entry<String, String>> iterator() {
		return new MapSetClientIterator();
	}

	public Iterator<String> get(String key) throws IOException {
		contains_set.clear();
		cacheletHash.getCacheletsFromKey(key, contains_set, numServers,
				replication);

		for (Integer cacheletID : contains_set) {
			if (list.get(cacheletID).contains(key)) {
				return list.get(cacheletID).get(key);
			}
		}

		return new NullIterator<String>();
	}

	public class MapSetClientIterator implements
			Iterator<Entry<String, String>> {

		private Iterator<MapSetCacheletConnection> connectionIter = null;
		private Iterator<Entry<String, String>> currIter = null;

		public MapSetClientIterator() {
			connectionIter = list.values().iterator();
			advanceConnectionIter();
		}

		private void advanceConnectionIter() {
			if (connectionIter.hasNext()) {
				currIter = connectionIter.next().iterator();
			} else {
				currIter = null;
			}
		}

		@Override
		public boolean hasNext() {
			return currIter != null && currIter.hasNext();
		}

		@Override
		public Entry<String, String> next() {
			do {
				if (currIter == null) {
					return null;
				} else if (currIter.hasNext()) {
					return currIter.next();
				} else {
					advanceConnectionIter();
				}
			} while (true);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"MapSetClientIterator::remove is not supported");
		}

	}
}