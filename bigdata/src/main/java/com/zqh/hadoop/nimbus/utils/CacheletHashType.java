package com.zqh.hadoop.nimbus.utils;

public enum CacheletHashType {
	/**
	 * Represents a hash code implementation of the Cachelet Hash. See
	 * {@link HashCodeCacheletHash}
	 */
	HASHCODE,

	/**
	 * Represents a CRC16 implementation of the Cachelet Hash. See
	 * {@link CRC16CacheletHash}
	 */
	CRC16,
	
	/**
	 * Represents a MurMur implementation of the Cachelet Hash. See
	 * {@link MurmurCacheletHash}
	 */
	MURMUR
}
