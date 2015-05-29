package com.zqh.akka.cleakka.test

import com.zqh.akka.cleakka.{CacheClient, CacheServer}
import org.specs2.mutable._

class ServerSpec extends Specification {
  "Server" should {
    "be started" in {
      val serverRef = CacheServer.start("x", 10)
      val client    = new CacheClient(serverRef)
      client.put("hi", "2")

      true must equalTo(true)
    }

    "be stopped" in {
      val serverRef = CacheServer.start("y", 10)
      val client    = new CacheClient(serverRef)
      client.stopServer()

      true must equalTo(true)
    }
  }
}
