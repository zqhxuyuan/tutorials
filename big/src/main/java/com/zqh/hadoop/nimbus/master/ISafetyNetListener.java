package com.zqh.hadoop.nimbus.master;

/**
 * Inherit from this class and register with the {@link NimbusSafetyNet} to
 * receive notifications on ZooKeeper related events.
 */
public interface ISafetyNetListener {

	/**
	 * Used by the {@link NimbusSafetyNet} whenever a Cache ZNode is added.
	 * 
	 * @param name
	 *            The name of the Cache that was added.
	 */
	public void onCacheAdded(String name);

	/**
	 * Used by the {@link NimbusSafetyNet} whenever a Cache ZNode is removed.
	 * 
	 * @param name
	 *            The name of the Cache that was removed.
	 */
	public void onCacheRemoved(String name);

	/**
	 * Used by the {@link NimbusSafetyNet} whenever a Cachelet ZNode is added.
	 * 
	 * @param cacheName
	 *            The name of the Cache that the Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that was added.
	 */
	public void onCacheletAdded(String cacheName, String cacheletName);

	/**
	 * Used by the {@link NimbusSafetyNet} whenever a Cachelet ZNode is removed.
	 * 
	 * @param cacheName
	 *            The name of the Cache that the Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that was removed.
	 */
	public void onCacheletRemoved(String cacheName, String cacheletName);

	/**
	 * Used by the {@link NimbusSafetyNet} whenever a Cachelet ZNode has gone
	 * stale. This means that the Safety Net has not recognized a heartbeat from
	 * a Cachelet within the configured amount of time.
	 * 
	 * @param cacheName
	 *            The name of the Cache that the Cachelet belongs to.
	 * @param cacheletName
	 *            The name of the Cachelet that has gone stale.
	 */
	public void onCacheletStale(String cacheName, String cacheletName);
}
