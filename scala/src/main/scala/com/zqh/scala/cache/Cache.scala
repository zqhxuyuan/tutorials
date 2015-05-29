package com.zqh.scala.cache

import java.util.concurrent.Callable

import com.google.common.cache.{Cache => GuavaCache}

/**
 * https://github.com/owainlewis/scala-cache
 */
sealed trait Caching[K <: AnyRef, V <: AnyRef] {

  def get(k: K): Option[V]

  def set(k: K, v: V): Unit

  def getOrElseUpdate(k: K, f: () => V): V

  def remove(k: K): Unit

  def clear(): Unit

  def size: Long
}

class Cache[K <: AnyRef, V <: AnyRef](cache: GuavaCache[K,V]) extends Caching[K, V] {

  /**
   * Get an optional value from the cache
   *
   * cache.set("foo", "bar")
   * cache.get("foo") => Some("bar")
   *
   * @param k Cache key
   */
  def get(k: K): Option[V] = {
    Option(cache.getIfPresent(k))
  }

  /**
   * Get a value from the cache or else return a default value
   *
   * @param k cache key
   * @param orElse the default value to return if k is not found in the cache
   */
  def getOrElse(k: K, orElse: V): V = {
    get(k) match {
      case Some(v) => v
      case None    => orElse
    }
  }

  /**
   * Get a value from the cache or call a function to set the cache value
   *
   * This can be used to make TTL caching easy to understand
   *
   * @param k the cache key
   * @param f a function to call if the cache value is not present
   * @return
   */
  def getOrElseUpdate(k: K, f: () => V): V = {
    cache.get(k, new Callable[V] {
      def call(): V = f()
    })
  }

  /**
   * Insert an item into the cache
   *
   * e.g cache.set("foo", "bar")
   *
   * @param k key
   * @param v value
   */
  def set(k: K, v: V) {
    cache.put(k, v)
  }

  /**
   * Evicts an item from the cache
   *
   * @param k the key to evict
   */
  def remove(k: K): Unit = {
    cache.invalidate(k)
  }

  /**
   * Clear all items in the cache
   *
   */
  def clear(): Unit = {
    cache.invalidateAll()
  }

  /**
   * Returns the size of the current cache
   *
   */
  def size: Long = {
    cache.size()
  }

  /**
   * Check if the cache contains a value for a given key
   *
   * @param k
   */
  def contains(k: K): Boolean = {
    get(k).isDefined
  }
}
