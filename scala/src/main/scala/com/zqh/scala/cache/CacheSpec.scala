package com.zqh.scala.cache

import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSpec, Matchers}

class CacheSpec extends FunSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll {

  describe("TTL Caching") {

    val cache = TTLCache[String, String](1)

    before {
      cache.clear()
    }

    it("should cache a value") {
      cache.size shouldBe 0
      cache.set("foo", "bar")
      cache.size shouldBe 1
      cache.get("foo") shouldBe Some("bar")
    }

    it("should expire a value from the cache after a given time") {
      cache.get("foo") shouldBe None
      cache.set("foo", "bar")
      cache.get("foo") shouldBe Some("bar")
      Thread.sleep(1000)
      cache.get("foo") shouldBe None
    }

    it("should return a default value if cache miss happens") {
      cache.getOrElse("foo", "baz") shouldBe "baz"
      cache.set("foo", "bar")
      cache.getOrElse("foo", "baz") shouldBe "bar"
    }

    it("should implement getOrElseUpdate") {
      var count = 0
      def f() = cache.getOrElseUpdate("foo", () => {
        count += 1
        "bar"
      })
      f(); f(); f()
      Thread.sleep(1000)
      f()
      count shouldBe 2
    }

    it("should implement contains") {
      cache.contains("foo") shouldBe false
      cache.set("foo", "bar")
      cache.contains("foo") shouldBe true
    }
  }
}