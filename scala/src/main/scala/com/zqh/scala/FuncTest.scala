package com.zqh.scala

/**
 * Created by zhengqh on 15/9/11.
 */
object FuncTest {

  def max = (a:Int, b:Int) => Math.max(a,b)
  val min = (a:Int, b:Int) => Math.min(a,b)

  def main(args: Array[String]) {
    println(min(1,2))
    println(max(1,2))
  }
}
