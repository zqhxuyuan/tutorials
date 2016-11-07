package com.zqh.hadoop.nimbus.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;
import com.zqh.hadoop.nimbus.utils.ChildZNodeWatcher;

import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

/**
 * The NimbusSafetyNet is a Singleton class to monitor ZooKeeper as it pertains
 * to Nimbus. It runs in it's own thread and monitors ZooKeeper for new Cache
 * and Cachelet ZNodes and notifies any listeners when ZNodes are added or
 * removed.<br>
 * <br>
 * Listeners need to inherit from {@link ISafetyNetListener} and then register
 * with the NimbusSafetyNet instance via
 * {@link NimbusSafetyNet#addListener(ISafetyNetListener)}.<br>
 * <br>
 * The NimbusSafetyNet also monitors heartbeats from Cachelets via ZooKeeper and
 * notifies listeners when they go stale, i.e. the Safety Net has not receives a
 * heartbeat based on the configured amount. <br>
 * <br>
 * This class itself is responsible for watching the Nimbus root ZNode and
 * creating/destroying {@link CacheZNode}s accordingly, as well as updating any
 * {@link CacheZNode}s.
 */
public class NimbusSafetyNet implements Runnable {

	private static final Logger LOG = Logger.getLogger(NimbusSafetyNet.class);

	private HashMap<String, CacheZNode> cacheMap = new HashMap<String, CacheZNode>();
	private ChildZNodeWatcher rootWatcher = new ChildZNodeWatcher();
	private ArrayList<ISafetyNetListener> listeners = new ArrayList<ISafetyNetListener>();

	private static NimbusSafetyNet s_instance = null;
	private boolean stopped = false;

	/**
	 * Gets the Singleton instance of the {@link NimbusSafetyNet}.
	 * 
	 * @return The {@link NimbusSafetyNet} instance.
	 */
	public static NimbusSafetyNet getInstance() {
		if (s_instance == null) {
			s_instance = new NimbusSafetyNet();
		}
		return s_instance;
	}

	private NimbusSafetyNet() {
	}

	/**
	 * Starts the Safety Net in it's own thread. This thread will constantly
	 * update the Safety Net, typically until a ZooKeeper error occurs or the
	 * Master service shuts down.
	 */
	@Override
	public void run() {
		try {
			LOG.info("Starting safety net...");
			List<String> caches = Nimbus.getZooKeeper().getChildren(
					Nimbus.ROOT_ZNODE, rootWatcher);
			updateCacheMap(caches);
		} catch (KeeperException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
			LOG.error(e.getMessage());
		}

		while (!stopped) {
			try {				
				update();
				Thread.sleep(NimbusConf.getConf().getCacheletHeartbeatInterval());
			} catch (KeeperException e) {
				e.printStackTrace();
				LOG.error(e.getMessage());
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOG.error(e.getMessage());
			}
		}

		LOG.info("Safety net offline");
	}

	public void stop() {
		this.stopped = true;
	}

	/**
	 * Checks if the watcher on the root node has been triggered and creates any
	 * {@link CacheZNode}s accordingly. Calls {@link CacheZNode#update()} for
	 * all instances it is storing.
	 * 
	 * @throws KeeperException
	 *             If the ZooKeeper server signals an error with a non-zero
	 *             error code.
	 * @throws InterruptedException
	 *             If the ZooKeeper server transaction is interrupted.
	 */
	private void update() throws KeeperException, InterruptedException {
		if (rootWatcher.isTriggered()) {

			LOG.info("Root watcher triggered");

			rootWatcher.reset();
			List<String> caches = Nimbus.getZooKeeper().getChildren(
					Nimbus.ROOT_ZNODE, rootWatcher);
			updateCacheMap(caches);
		}

		for (CacheZNode node : cacheMap.values()) {
			node.update();
		}
	}

	/**
	 * Helper function to updates the safety net's map of {@link CacheZNode}s.
	 * Will create/destroy instances and notify any listeners. <br>
	 * <br>
	 * Any Caches not represented by a {@link CacheZNode} that are present in
	 * the given map will be created, and any {@link CacheZNode}s not present in
	 * the map will be destroyed.
	 * 
	 * @param caches
	 *            The up-to-date list of Cache names.
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void updateCacheMap(List<String> caches) throws KeeperException,
			InterruptedException {
		for (String cacheName : caches) {
			CacheZNode node = cacheMap.get(cacheName);
			if (node == null) {
				node = new CacheZNode(cacheName);
				cacheMap.put(cacheName, node);
				notifyCacheAdded(cacheName);
			}
		}

		ArrayList<String> toRemove = new ArrayList<String>();
		for (Entry<String, CacheZNode> e : cacheMap.entrySet()) {
			if (!caches.contains(e.getKey())) {
				e.getValue().clear();
				toRemove.add(e.getKey());
			}
		}

		for (String cacheName : toRemove) {
			notifyCacheRemoved(cacheName);
			cacheMap.remove(cacheName);
		}
	}

	/**
	 * Registers the given {@link ISafetyNetListener} to receive notifications
	 * related to the safety net.
	 * 
	 * @param listener
	 *            The {@link ISafetyNetListener} to add.
	 * @return If the operation was successful.
	 */
	public synchronized boolean addListener(ISafetyNetListener listener) {
		return listeners.add(listener);
	}

	/**
	 * Unregisters the given {@link ISafetyNetListener}.
	 * 
	 * @param listener
	 *            The {@link ISafetyNetListener} to remove.
	 * @return If the operation was successful.
	 */
	public synchronized boolean removeListener(ISafetyNetListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Used by the {@link CacheZNode} to notify all listeners that a Cache ZNode
	 * was added to ZooKeeper.
	 * 
	 * @param name
	 *            The name of the Cache that was added.
	 */
	public synchronized void notifyCacheAdded(String name) {
		LOG.info("Cache added: " + name);
		for (ISafetyNetListener listener : listeners) {
			listener.onCacheAdded(name);
		}
	}

	/**
	 * Used by the {@link CacheZNode} to notify all listeners that a Cache ZNode
	 * was removed from ZooKeeper.
	 * 
	 * @param name
	 *            The name of the Cache that was removed.
	 */
	public synchronized void notifyCacheRemoved(String name) {
		LOG.info("Cache removed: " + name);
		for (ISafetyNetListener listener : listeners) {
			listener.onCacheRemoved(name);
		}
	}

	/**
	 * Used by the {@link CacheletZNode} to notify all listeners that a Cachelet
	 * ZNode was added to ZooKeeper.
	 * 
	 * @param cacheName
	 *            The name of the Cache that Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that was added.
	 */
	public synchronized void notifyCacheletAdded(String cacheName,
			String cacheletName) {
		LOG.info("Cachelet added: " + cacheName + "\t" + cacheletName);
		for (ISafetyNetListener listener : listeners) {
			listener.onCacheletAdded(cacheName, cacheletName);
		}
	}

	/**
	 * Used by the {@link CacheletZNode} to notify all listeners that a Cachelet
	 * ZNode was removed from ZooKeeper.
	 * 
	 * @param cacheName
	 *            The name of the Cache that Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that was removed.
	 */
	public synchronized void notifyCacheletRemoved(String cacheName,
			String cacheletName) {
		LOG.info("Cachelet removed: " + cacheName + "\t" + cacheletName);
		for (ISafetyNetListener listener : listeners) {
			listener.onCacheletRemoved(cacheName, cacheletName);
		}
	}

	/**
	 * Used by the {@link CacheletZNode} to notify all listeners that a Cachelet
	 * has not sent a HeartBeat to ZooKeeper within the configured amount of
	 * time.
	 * 
	 * @param cacheName
	 *            The name of the Cache that Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that was removed.
	 */
	public synchronized void notifyCacheletStale(String cacheName,
			String cacheletName) {
		LOG.info("Cachelet stale: " + cacheName + "\t" + cacheletName);
		for (ISafetyNetListener listener : listeners) {
			listener.onCacheletStale(cacheName, cacheletName);
		}
	}
}
