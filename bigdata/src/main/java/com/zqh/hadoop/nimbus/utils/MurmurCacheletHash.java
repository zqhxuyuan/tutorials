package com.zqh.hadoop.nimbus.utils;

import java.util.Set;

/**
 * The Murmur algorithm is not as fast as the {@link HashCodeCacheletHash} but
 * provides a more even distribution of members. It is faster than the
 * {@link CRC16CacheletHash}. Much like the HashCodeCacheletHash, the key is
 * hashed and then 1 is added to that hash to determine the replication for as
 * many cachelets as needed
 */
public class MurmurCacheletHash extends ICacheletHash {

	private static int seed = 0x124feac3;

	@Override
	public void getCacheletsFromKey(String key, Set<Integer> set,
			int numCachelets, int replication) {

		int hash = Math.abs(hash(BytesUtil.toBytes(key))) % numCachelets;
		for (int i = 0; i < replication; ++i) {
			set.add(hash);
			hash = (hash + 1) % numCachelets;
		}
	}

	// 'm' and 'r' are mixing constants generated offline.
	// They're not really 'magic', they just happen to work well.
	private static final int m = 0x5bd1e995;
	private static final int r = 24;
	private int len, h, i, k;
	private static final int FF = 0xFF;

	private int hash(byte[] data) {
		// Initialize the hash to a 'random' value
		len = data.length;
		h = seed ^ len;

		i = 0;
		while (len >= 4) {
			k = data[i + 0] & FF;
			k |= (data[i + 1] & FF) << 8;
			k |= (data[i + 2] & FF) << 16;
			k |= (data[i + 3] & FF) << 24;

			k *= m;
			k ^= k >>> r;
			k *= m;

			h *= m;
			h ^= k;

			i += 4;
			len -= 4;
		}

		switch (len) {
		case 3:
			h ^= (data[i + 2] & 0xFF) << 16;
		case 2:
			h ^= (data[i + 1] & 0xFF) << 8;
		case 1:
			h ^= (data[i + 0] & 0xFF);
			h *= m;
		}

		h ^= h >>> 13;
		h *= m;
		h ^= h >>> 15;

		return h;
	}

}
