package com.zqh.hadoop.nimbus.master;

import com.zqh.hadoop.nimbus.main.Nimbus;
import com.zqh.hadoop.nimbus.main.NimbusConf;

import org.apache.zookeeper.KeeperException;

import com.zqh.hadoop.nimbus.utils.DataZNodeWatcher;

/**
 * A Cachelet representation of a ZNode.<br>
 * <br>
 * Watches for any Data updates on the Cachelet and notifies listeners if the
 * data goes stale. Stale in this case is when the elapsed trigger time is
 * greater than the safety net timeout as described in {@link com.zqh.hadoop.nimbus.main.NimbusConf}.
 */
public class CacheletZNode {

	private String cacheName = null;
	private String cacheletName = null;
	private String cacheletPath = null;

	private DataZNodeWatcher datawatcher = new DataZNodeWatcher();

	/**
	 * Initializes a new instance of a CacheletZNode.
	 * 
	 * @param cacheName
	 *            The Cache this Cachelet belongs to.
	 * @param cacheletName
	 *            The Cachelet name.
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public CacheletZNode(String cacheName, String cacheletName)
			throws KeeperException, InterruptedException {
		this.cacheName = cacheName;
		this.cacheletName = cacheletName;
		this.cacheletPath = Nimbus.ROOT_ZNODE + "/" + cacheName + "/"
				+ cacheletName;

		Nimbus.getZooKeeper().getDataVariable(cacheletPath, datawatcher, null);
	}

	/**
	 * Checks to see if the watcher has been triggered and will notify any
	 * listeners if the Cachelet has timed out.
	 * 
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	public void update() throws KeeperException, InterruptedException {
		if (datawatcher.isDeleted()) {
			return;
		} else if (datawatcher.isTriggered()) {
			datawatcher.reset();
			Nimbus.getZooKeeper().getDataVariable(cacheletPath, datawatcher,
					null);
		} else if (datawatcher.getElapsedTriggerTime() > NimbusConf.getConf()
				.getSafetyNetTimeout() && !datawatcher.getNotified()) {
			NimbusSafetyNet.getInstance().notifyCacheletStale(cacheName,
					cacheletName);
			datawatcher.setNotified(true);
		}
	}
}
