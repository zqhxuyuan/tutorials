package com.github.NoahShen.jue.util;

import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 支持并发的高效的LRU缓存
 * 
 * @author noah
 * 
 * @param <K>
 * @param <V>
 */
public class ConcurrentLRUCache<K, V> {

	/**
	 * 最大片段数
	 */
	static final int MAX_SEGMENTS = 1 << 16;

	/**
	 * 默认的最大K-V数量
	 */
	static final int MAX_CAPACITY = 1 << 16;

	/**
	 * 默认片段数
	 */
	public static final int DEFAULT_SEGMENTS = 16;

	/**
	 * 默认的加载因子
	 */
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * Map片
	 */
	private SegmentMap<K, V>[] segmentMaps;

	/**
	 * 最大K-V数
	 */
	private int maxCapacity;

	/**
	 * 初始化
	 */
	public ConcurrentLRUCache() {
		this(MAX_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_SEGMENTS);
	}

	/**
	 * 初始化
	 * 
	 * @param maxCapacity
	 *            最大K-V数
	 */
	public ConcurrentLRUCache(int maxCapacity) {
		this(maxCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_SEGMENTS);
	}

	/**
	 * 初始化
	 * 
	 * @param maxCapacity
	 *            最大K-V数
	 * @param loadFactor
	 *            加载因子
	 */
	public ConcurrentLRUCache(int maxCapacity, float loadFactor) {
		this(maxCapacity, loadFactor, DEFAULT_SEGMENTS);
	}

	/**
	 * 初始化
	 * 
	 * @param maxCapacity
	 *            最大K-V数
	 * @param loadFactor
	 *            加载因子
	 * @param segments
	 *            Map片数
	 */
	public ConcurrentLRUCache(int maxCapacity, float loadFactor, int segments) {
		if (!(loadFactor > 0) || maxCapacity < 0 || segments <= 0) {
			throw new IllegalArgumentException();
		}
		if (maxCapacity > MAX_CAPACITY) {
			maxCapacity = MAX_CAPACITY;
		}
		if (segments > MAX_SEGMENTS) {
			segments = MAX_SEGMENTS;
		}
		this.maxCapacity = maxCapacity;
		// 初始化每个Map片的K-V数
		int cap = maxCapacity / segments;
		while (cap * segments < maxCapacity) {
			++cap;
		}
		// 初始化Map片
		segmentMaps = newSegmentMapsArray(segments);
		for (int i = 0; i < this.segmentMaps.length; ++i) {
			this.segmentMaps[i] = new SegmentMap<K, V>(cap, loadFactor);
		}
	}

	/**
	 * 计算哈希值
	 * 
	 * @param h
	 * @return
	 */
	private int hash(int h) {
		h += (h << 15) ^ 0xffffcd7d;
		h ^= (h >>> 10);
		h += (h << 3);
		h ^= (h >>> 6);
		h += (h << 2) + (h << 14);
		return h ^ (h >>> 16);
	}

	/**
	 * 根据Hash值获取对应的Map片
	 * 
	 * @param hash
	 * @return
	 */
	private SegmentMap<K, V> segmentMapFor(int hash) {
		return this.segmentMaps[hash & (this.segmentMaps.length - 1)];
	}

	/**
	 * 创建一个Map片数组
	 * 
	 * @param <K>
	 * @param <V>
	 * @param i
	 * @return
	 */
	@SuppressWarnings( { "unchecked" })
	private SegmentMap<K, V>[] newSegmentMapsArray(int i) {
		return new SegmentMap[i];
	}

	/**
	 * 移除所有元素
	 */
	public void clear() {
		for (int i = 0; i < this.segmentMaps.length; ++i) {
			this.segmentMaps[i].clear();
		}
	}

	/**
	 * 是否存在该主键
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(Object key) {
		int hash = hash(key.hashCode());
		return segmentMapFor(hash).containsKey(key);
	}

	/**
	 * 获取key对应的值
	 * 
	 * @param key
	 * @return
	 */
	public V get(Object key) {
		int hash = hash(key.hashCode());
		return segmentMapFor(hash).get(key);
	}

	/**
	 * 是否为空
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		final SegmentMap<K, V>[] segmentMaps = this.segmentMaps;
		for (int i = 0; i < segmentMaps.length; ++i) {
			final SegmentMap<K, V> segmentMap = segmentMaps[i];
			if (!segmentMap.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 存入一个键值
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public V put(K key, V value) {
		int hash = hash(key.hashCode());
		return segmentMapFor(hash).put(key, value);
	}

	/**
	 * 移除一个键对应的值
	 * 
	 * @param key
	 * @return
	 */
	public V remove(Object key) {
		int hash = hash(key.hashCode());
		return segmentMapFor(hash).remove(key);
	}

	/**
	 * 返回K-V键值数量
	 * 
	 * @return
	 */
	public int size() {
		final SegmentMap<K, V>[] segmentMaps = this.segmentMaps;
		int size = 0;
		for (int i = 0; i < segmentMaps.length; ++i) {
			size += segmentMaps[i].size();
		}
		return size;
	}
	
	/**
	 * 是否删除最老的数据
	 * @return
	 */
	protected boolean removeEldestEntry() {
		return this.size() > this.maxCapacity;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < segmentMaps.length; ++i) {
			buffer.append(segmentMaps[i].size()).append(segmentMaps[i]).append("\n");
		}
		return buffer.toString();
	}

	/**
	 * ConcurrentLRUCache的Map片
	 * 
	 * @author noah
	 * 
	 * @param <K>
	 * @param <V>
	 */
	@SuppressWarnings("hiding")
	private final class SegmentMap<K, V> extends LinkedHashMap<K, V> {

		/**
		 * 
		 */
		private static final long serialVersionUID = -75326095114490496L;

		/**
		 * 读写锁
		 */
		private final Lock lock = new ReentrantLock();

		/**
		 * 构造一个Map片
		 * 
		 * @param initCapacity
		 *            初始K-V数
		 * @param loadFactor
		 *            加载因子
		 */
		public SegmentMap(int initCapacity, float loadFactor) {
			super(initCapacity, loadFactor, true);
		}

		@Override
		protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
			return ConcurrentLRUCache.this.removeEldestEntry();
		}

		@Override
		public boolean containsKey(Object key) {
			try {
				lock.lock();
				return super.containsKey(key);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public boolean containsValue(Object value) {
			try {
				lock.lock();
				return super.containsValue(value);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public V get(Object key) {
			try {
				lock.lock();
				return super.get(key);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public V put(K key, V value) {
			try {
				lock.lock();
				return super.put(key, value);
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void clear() {
			try {
				lock.lock();
				super.clear();
			} finally {
				lock.unlock();
			}
		}
	}


}
