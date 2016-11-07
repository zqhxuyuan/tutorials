package com.zqh.hadoop.nimbus.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.utils.ChildZNodeWatcher;

import org.apache.zookeeper.KeeperException;

/**
 * A Cache representation of a ZNode.<br>
 * <br>
 * Watches for any Cachelet ZNode updates. Notifies listeners whenever a
 * Cachelet is added or removed.
 */
public class CacheZNode {

	private HashMap<String, CacheletZNode> cacheletMap = new HashMap<String, CacheletZNode>();
	private ChildZNodeWatcher cacheletWatcher = null;
	private String cacheZNode = null;
	private String cacheName = null;

	public CacheZNode(String cacheName) throws KeeperException,
			InterruptedException {
		this.cacheName = cacheName;
		cacheZNode = Nimbus.ROOT_ZNODE + "/" + this.cacheName;
		cacheletWatcher = new ChildZNodeWatcher();
		updateCacheletMap(Nimbus.getZooKeeper().getChildren(cacheZNode,
				cacheletWatcher));
	}

	/**
	 * Checks of the Cachelet watcher has been triggered and will create/remove
	 * {@link CacheletZNode}s when necessary. <br>
	 * <br>
	 * Notifies any listeners if Cachelets are added or removed and updates any
	 * CacheletZNodes.
	 * 
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void update() throws KeeperException, InterruptedException {
		if (cacheletWatcher != null) {
			if (cacheletWatcher.isDeleted()) {
				return;
			} else if (cacheletWatcher.isTriggered()) {
				cacheletWatcher.reset();
				updateCacheletMap(Nimbus.getZooKeeper().getChildren(cacheZNode,
						cacheletWatcher));
			}

			for (CacheletZNode node : cacheletMap.values()) {
				node.update();
			}
		}
	}

	/**
	 * Clears any Cachelets and stops the watch.
	 */
	public void clear() {
		cacheletMap.clear();
		cacheletWatcher = null;
	}

	/**
	 * Helper function to updates this Cache's {@link CacheletZNode}s. Will
	 * create/destroy instances and notify any listeners. <br>
	 * <br>
	 * Any Cachelets not represented by a CacheletZNode that are present in the
	 * given map will be created, and any CacheletZNodes not present in the map
	 * will be destroyed.
	 * 
	 * @param cachelets
	 *            The up-to-date list of Cachelet names.
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private void updateCacheletMap(List<String> cachelets)
			throws KeeperException, InterruptedException {
		for (String cacheletName : cachelets) {
			CacheletZNode node = cacheletMap.get(cacheletName);
			if (node == null) {
				node = new CacheletZNode(cacheName, cacheletName);
				cacheletMap.put(cacheletName, node);
				NimbusSafetyNet.getInstance().notifyCacheletAdded(cacheName,
						cacheletName);
			}
		}

		ArrayList<String> toRemove = new ArrayList<String>();
		for (Entry<String, CacheletZNode> e : cacheletMap.entrySet()) {
			if (!cachelets.contains(e.getKey())) {
				toRemove.add(e.getKey());
			}
		}

		for (String cacheletName : toRemove) {
			NimbusSafetyNet.getInstance().notifyCacheletRemoved(cacheName,
					cacheletName);
			cacheletMap.remove(cacheletName);
		}
	}
}
