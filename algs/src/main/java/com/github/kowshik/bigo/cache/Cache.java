package com.github.kowshik.bigo.cache;

/**
 * A simple com.github.kowshik.bigo.cache interface.
 * 
 * @param <CacheKeyType>
 *            Type of com.github.kowshik.bigo.cache key
 * @param <CacheValueType>
 *            Type of com.github.kowshik.bigo.cache value
 */
public interface Cache<CacheKeyType, CacheValueType> {
	CacheValueType get(CacheKeyType key);

	void put(CacheKeyType key, CacheValueType value);

	int getSize();
}
