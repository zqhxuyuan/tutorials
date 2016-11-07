package com.zqh.hadoop.nimbus.client;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.master.CacheInfo;
import com.zqh.hadoop.nimbus.master.NimbusMaster;

import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import com.zqh.hadoop.nimbus.utils.BigBitArray;
import com.zqh.hadoop.nimbus.utils.ICacheletHash;
import com.zqh.hadoop.nimbus.utils.DataZNodeWatcher;
import com.zqh.hadoop.nimbus.master.CacheDoesNotExistException;
import com.zqh.hadoop.nimbus.nativestructs.Triple;

public class TripleSetClient {

	private static final Logger LOG = Logger.getLogger(TripleSetClient.class);

	private HashMap<Integer, TripleSetCacheletConnection> list = new HashMap<Integer, TripleSetCacheletConnection>();
	private int numServers = -1;

	private HashSet<Integer> contains_set = new HashSet<Integer>();
	private TripleSetCacheletConnection tempConnection;
	private ICacheletHash cacheletHash = ICacheletHash.getInstance();
	private BigBitArray availabilityArray = null;
	private DataZNodeWatcher watcher = new DataZNodeWatcher();
	private int contains_numdown = 0;
	private String cacheName = null;
	private int replication;

	/**
	 * Initializes a connection to a Nimbus distributed triple set.
	 * 
	 * @param cacheName
	 *            The Cache to connect to.
	 * @throws CacheDoesNotExistException
	 * @throws IOException
	 *             If the Bloom filters do not exist.
	 */
	public TripleSetClient(String cacheName) throws CacheDoesNotExistException,
			IOException {
		this.cacheName = cacheName;
		this.replication = NimbusConf.getConf().getReplicationFactor();
		String[] cachelets = NimbusConf.getConf().getNimbusCacheletAddresses()
				.split(",");
		for (int i = 0; i < cachelets.length; ++i) {
			list.put(i,
					new TripleSetCacheletConnection(cacheName, cachelets[i]));
		}

		numServers = cachelets.length;

		CacheInfo info = new CacheInfo(Nimbus.getZooKeeper().getDataVariable(
				cacheName, watcher, null));
		availabilityArray = new BigBitArray(info.getAvailabilityArray());
	}

	/**
	 * Ingests the given absolute path into the Cache based on the given
	 * parameters.
	 * 
	 * @param p
	 *            The file to ingest. Each line in the file is considered a
	 *            record. The path must be an absolute path.
	 * @throws IOException
	 *             If the path is not absolute.
	 * @throws CacheDoesNotExistException
	 *             If the Cache does not exist.
	 */
	public void read(Path p) throws IOException, CacheDoesNotExistException {

		if (!p.isAbsolute()) {
			throw new IOException("Use absolute paths");
		}

		NimbusMaster.getInstance().getCacheInfoLock(cacheName);
		CacheInfo info = NimbusMaster.getInstance().getCacheInfo(cacheName);
		info.setFilename(p.toString());
		NimbusMaster.getInstance().setCacheInfo(cacheName, info);
		NimbusMaster.getInstance().releaseCacheInfoLock(cacheName);

		do {
			if (watcher.isTriggered()) {
				try {
					watcher.reset();
					info = new CacheInfo(Nimbus.getZooKeeper().getDataVariable(
							cacheName, watcher, null));
					availabilityArray = new BigBitArray(
							info.getAvailabilityArray());

					int numon = 0;
					for (int i = 0; i < list.values().size(); ++i) {
						if (availabilityArray.isBitOn(i)) {
							++numon;
						}
					}

					if (numon >= NimbusConf.getConf().getNumNimbusCachelets()
							- (NimbusConf.getConf().getReplicationFactor() - 1)) {
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} while (true);
	}

	/**
	 * Adds the given triple to the set
	 * 
	 * @param element
	 *            the Triple to add to the set
	 * @throws CacheDoesNotExistException
	 *             If the Cache does not exist.
	 */
	public boolean add(Triple element) throws CacheletsUnavailableException {

		checkWatch();

		contains_set.clear();
		cacheletHash.getCacheletsFromKey(element.toString(), contains_set,
				numServers, replication);

		contains_numdown = 0;
		for (Integer cacheletID : contains_set) {
			if (availabilityArray.isBitOn(cacheletID)) {
				tempConnection = list.get(cacheletID);
				try {
					return tempConnection.add(element);
				} catch (CacheletNotConnectedException e) {
					try {
						LOG.info("Caught CacheletNotConnectedException for ID "
								+ cacheletID + ".  Attempting reconnect...");
						tempConnection.connect();

						LOG.info("Successfully reconnected to ID " + cacheletID);
						if (++contains_numdown == replication) {
							throw new CacheletsUnavailableException();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						if (++contains_numdown == replication) {
							throw new CacheletsUnavailableException();
						}
					}
				} catch (IOException e) {
					LOG.error("Received error from Cachelet ID " + cacheletID
							+ ": " + e.getMessage());
					LOG.error("Disconnecting.");
					try {
						tempConnection.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					availabilityArray.set(cacheletID, false);
					if (++contains_numdown == replication) {
						throw new CacheletsUnavailableException();
					}
				}
			} else {
				if (++contains_numdown == replication) {
					throw new CacheletsUnavailableException();
				}
			}
		}

		return false;
	}

	public Iterator<Triple> iterator() throws IOException {
		StreamingTripleSetIterator iter = new StreamingTripleSetIterator();
		for (int i = 0; i < numServers; ++i) {
			tempConnection = list.get(i);
			tempConnection.getAll();
			iter.addClient(tempConnection);
		}
		iter.initialize();
		return iter;
	}

	public Iterator<Triple> iterator(String s1) throws IOException {
		StreamingTripleSetIterator iter = new StreamingTripleSetIterator();
		for (int i = 0; i < numServers; ++i) {
			tempConnection = list.get(i);
			tempConnection.get(s1);
			iter.addClient(tempConnection);
		}
		iter.initialize();
		return iter;
	}

	public Iterator<Triple> iterator(String s1, String s2) throws IOException {
		StreamingTripleSetIterator iter = new StreamingTripleSetIterator();
		for (int i = 0; i < numServers; ++i) {
			tempConnection = list.get(i);
			tempConnection.get(s1, s2);
			iter.addClient(tempConnection);
		}
		iter.initialize();
		return iter;
	}

	/**
	 * Determines if a given element is a member of the triple set.
	 * 
	 * @param element
	 *            The element to test.
	 * @return Whether or not this element is a member of the triple set.
	 * @throws IOException
	 *             If an error occurs when communicating with the Cachelets
	 * @throws CacheletsUnavailableException
	 *             If all the Cachelets that would store the given element are
	 *             unavailable.
	 */
	public boolean contains(Triple element)
			throws CacheletsUnavailableException {

		checkWatch();

		contains_set.clear();
		cacheletHash.getCacheletsFromKey(element.toString(), contains_set,
				numServers, replication);

		contains_numdown = 0;
		for (Integer cacheletID : contains_set) {
			if (availabilityArray.isBitOn(cacheletID)) {
				tempConnection = list.get(cacheletID);
				try {
					return tempConnection.contains(element);
				} catch (CacheletNotConnectedException e) {
					try {
						LOG.info("Caught CacheletNotConnectedException for ID "
								+ cacheletID + ".  Attempting reconnect...");
						tempConnection.connect();

						LOG.info("Successfully reconnected to ID " + cacheletID);
						if (++contains_numdown == replication) {
							throw new CacheletsUnavailableException();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						if (++contains_numdown == replication) {
							throw new CacheletsUnavailableException();
						}
					}
				} catch (IOException e) {
					LOG.error("Received error from Cachelet ID " + cacheletID
							+ ": " + e.getMessage());
					LOG.error("Disconnecting.");
					try {
						tempConnection.disconnect();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					availabilityArray.set(cacheletID, false);
					if (++contains_numdown == replication) {
						throw new CacheletsUnavailableException();
					}
				}
			} else {
				if (++contains_numdown == replication) {
					throw new CacheletsUnavailableException();
				}
			}
		}

		return false;
	}

	/**
	 * Determines if a collection of Triples are all elements of this set.
	 * 
	 * @param c
	 *            The collection to test.
	 * @return True if all elements are members of the set, false otherwise.
	 * @throws IOException
	 */
	public boolean containsAll(Collection<Triple> c) throws IOException {
		for (Triple t : c) {
			if (!contains(t)) {
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

		checkWatch();

		for (TripleSetCacheletConnection set : list.values()) {
			if (!set.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Disconnects this set from all Cachelets.
	 */
	public void disconnect() {
		for (TripleSetCacheletConnection worker : list.values()) {
			try {
				worker.disconnect();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		tempConnection = null;
	}

	private void checkWatch() {
		if (watcher.isTriggered()) {
			try {
				watcher.reset();

				CacheInfo info = new CacheInfo(Nimbus.getZooKeeper()
						.getDataVariable(cacheName, watcher, null));
				availabilityArray = new BigBitArray(info.getAvailabilityArray());

				for (int i = 0; i < list.values().size(); ++i) {
					if (!availabilityArray.isBitOn(i)
							&& list.get(i).isConnected()) {
						list.get(i).disconnect();
						LOG.info("Disconnecting " + i
								+ " due to Watch triggered.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}