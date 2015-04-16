package com.zqh.hadoop.nimbus.utils;

import java.util.HashSet;
import java.util.Set;

import com.zqh.hadoop.nimbus.main.NimbusConf;


/**
 * This abstract class is utilized by Cachelets to determine which members to
 * keep inside the set. It handles both distribution and replication of each
 * member. It is also used by clients to determine where a particular element
 * would reside.
 */
public abstract class ICacheletHash {

	private static Set<Integer> set = new HashSet<Integer>();
	private static ICacheletHash s_instance = null;

	/**
	 * Gets the Singleton instance of the Hash.
	 * 
	 * @return The Cachelet hash algorithm
	 * @throws RuntimeException If an invalid {@link CacheletHashType} is given.
	 */
	public static ICacheletHash getInstance() {
		if (s_instance == null) {
			switch (NimbusConf.getConf().getCacheletHashType()) {
			case HASHCODE:
				s_instance = new HashCodeCacheletHash();
				break;
			case CRC16:
				s_instance = new CRC16CacheletHash();
				break;
			case MURMUR:
				s_instance = new MurmurCacheletHash();
				break;
			default:
				throw new RuntimeException("Invalid CacheletHashType: "
						+ NimbusConf.getConf().getCacheletHashType());
			}
		}
		return s_instance;
	}

	public static void destroyInstance() {
		s_instance = null;
	}

	/**
	 * Hashes the given key and returns a list of Cachelet IDs that should store
	 * the key. The number of elements in the given set is determined by the
	 * replication parameter.
	 * 
	 * @param key
	 *            The key to hash.
	 * @param set
	 *            A set to populate with Cachelet IDs.
	 * @param numCachelets
	 *            The total number of Cachelets in the cluster.
	 * @param replication
	 *            The replication factor for the Cache.
	 */
	public abstract void getCacheletsFromKey(String key, Set<Integer> set,
			int numCachelets, int replication);

	/**
	 * Given a Cachelet index, key, and the total number of Cachelets, return a
	 * boolean value that says whether or not this key should be on the
	 * Cachelet.
	 * 
	 * @param cacheletID
	 *            The unique Cachelet ID
	 * @param key
	 *            The given key to hash.
	 * @param numCachelets
	 *            The total number of Cachelets
	 * @param replication
	 *            The replication factor for the Cache.
	 * @return Whether or not the key should be stored on the Cachelet.
	 */
	public boolean isValidCachelet(int cacheletID, String key,
			int numCachelets, int replication) {
		set.clear();
		getCacheletsFromKey(key, set, numCachelets, replication);

		for (Integer i : set) {
			if (i == cacheletID) {
				return true;
			}
		}

		return false;
	}
}