package com.zqh.scala.cache

import java.util.concurrent.TimeUnit

import com.google.common.cache.{Cache => GuavaCache, CacheBuilder}

object TTLCache {

  /**
   * Builds a TTL Cache store
   *
   * @param duration the TTL in seconds
   */
  def apply[K <: AnyRef, V <: AnyRef](implicit duration: Int) = {
    val ttlCache: GuavaCache[K, V] =
      CacheBuilder
        .newBuilder()
        .expireAfterWrite(duration, TimeUnit.SECONDS)
        .build()
    new Cache(ttlCache)
  }
}