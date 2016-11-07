package example

import java.net.InetSocketAddress

import net.spy.memcached.MemcachedClient
import org.junit.Assert._
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
 * Created by zhengqh on 15/8/13.
 */
class CacheTest extends AssertionsForJUnit{

  val server = new InetSocketAddress("192.168.6.201",11211)
  val cacheClient = new MemcachedClient(server)

  @Test
  def testNotNullFuture(): Unit ={
    cacheClient.add("k1", 3600, "v1")

    val future1 = cacheClient.asyncGet("k1")
    val future3 = cacheClient.asyncGet("k3")

    assert(future1 != null)
    assert(future3 != null)

    assertEquals("v1", future1.get)
    assertEquals(null, future3.get)
  }
}
