package com.zqh.hadoop.nimbus.server;

/**
 * An enumeration for which type of Cache is running.
 */
public enum CacheType {
	/**
	 * The Master Cache type is the Master service as described in more detail
	 * in {@link NimbusMaster}.
	 */
	MASTER,

	/**
	 * The Static Set is a Cache that evenly distributes a large data set over
	 * all Cachelets in the Cache based on a static file from HDFS
	 */
	STATIC_SET,

	/**
	 * The Dynamic Set is a Cache that evenly distributes a large data set over
	 * all Cachelets in the Cache.
	 */
	DYNAMIC_SET,

	/**
	 * The triple store is a Cache that distributes a large data set of Triples
	 * over all Cachelets in the Cache
	 */
	TRIPLE_STORE,

	MAPSET,

	DYNAMIC_MAP
}
