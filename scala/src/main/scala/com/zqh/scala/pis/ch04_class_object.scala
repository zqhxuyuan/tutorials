package com.zqh.scala.pis

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch04_class_object {

  class ChecksumAccumulator {
    var sum = 0
  }
  val acc = new ChecksumAccumulator
  val csa = new ChecksumAccumulator
  acc.sum = 3

  class ChecksumAccumulator2 {
    private var sum = 0
    def add(b: Byte): Unit = {
      sum += b
    }
    def checksum(): Int = {
      return ~(sum & 0xFF) + 1
    }
  }

  class ChecksumAccumulator4 {
    private var sum = 0
    def add(b: Byte) { sum += b }
    def checksum(): Int = ~(sum & 0xFF) + 1
  }


  import scala.collection.mutable.Map

  // 类 ChecksumAccumulator 的伴生对象
  object ChecksumAccumulator {
    private val cache = Map[String, Int]()
    def calculate(s: String): Int =
      if (cache.contains(s))
        cache(s)
      else {
        val acc = new ChecksumAccumulator4
        for (c <- s)
          acc.add(c.toByte)
        val cs = acc.checksum()
        cache += (s -> cs)
        cs
      }
  }

  import ChecksumAccumulator.calculate
  def main(args: Array[String]) {
    for (arg <- args)
      println(arg + ": " + calculate(arg))
  }

}
