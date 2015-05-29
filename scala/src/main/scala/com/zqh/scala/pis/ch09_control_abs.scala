package com.zqh.scala.pis

import java.io.{PrintWriter, File}

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch09_control_abs {

  def main(args: Array[String]) {

  }

  object FileMatcher {
    private def filesHere = (new java.io.File(".")).listFiles

    def filesEnding(query: String) =
      for (file <- filesHere; if file.getName.endsWith(query))
      yield file

    def filesContaining(query: String) =
      for (file <- filesHere; if file.getName.contains(query))
      yield file

    def filesRegex(query: String) =
      for (file <- filesHere; if file.getName.matches(query))
      yield file
  }

  object FileMatcher2 {
    private def filesHere = (new java.io.File(".")).listFiles

    def filesMatching(query: String, matcher: (String, String) => Boolean) = {
      for (file <- filesHere; if matcher(file.getName, query))
      yield file
    }

    def filesEnding(query: String)      = filesMatching(query, (fileName: String, query: String) => fileName.endsWith(query))
    def filesContaining(query: String)  = filesMatching(query,(fileName, query) => fileName.contains(query))
    def filesRegex(query: String)       = filesMatching(query, _.matches(_))
  }

  // 9.1 使用闭包减少代码重复
  object FileMatcher3 {
    private def filesHere = (new java.io.File(".")).listFiles

    private def filesMatching(matcher: String => Boolean) =
      for (file <- filesHere; if matcher(file.getName))
      yield file

    def filesEnding(query: String)      = filesMatching(_.endsWith(query))
    def filesContaining(query: String)  = filesMatching(_.contains(query))
    def filesRegex(query: String)       = filesMatching(_.matches(query))
  }


  // 9.2 简化客户代码
  def containsNeg(nums: List[Int]): Boolean = {
    var exists = false
    for (num <- nums)
      if (num < 0)
        exists = true
    exists
  }

  containsNeg(List(1, 2, 3, 4))
  containsNeg(List(1, 2, 3, -4))

  def containsNeg2(nums: List[Int]) = nums.exists(_ < 0)
  containsNeg2(Nil)
  containsNeg2(List(0, 1, -2))

  def containsOdd(nums: List[Int]): Boolean = {
    var exists = false
    for (num <- nums)
      if (num % 2 == 1)
        exists = true
    exists
  }
  def containsOdd2(nums: List[Int]) = nums.exists(_ % 2 == 1)


  // 9.3 Curry 化
  // 定义和调用“陈旧的”函数
  def plainOldSum(x: Int, y: Int) = x + y
  plainOldSum(1, 2)

  // 定义和调用 curry 化的函数
  def curriedSum(x: Int)(y: Int) = x + y
  curriedSum(1)(2)

  def first(x: Int) = (y: Int) => x + y
  val second = first(1)
  second(2)

  val onePlus = curriedSum(1)_
  onePlus(2)

  val twoPlus = curriedSum(2)_
  twoPlus(2)


  // 9.4 编写新的控制结构
  def twice(op: Double => Double, x: Double) = op(op(x))
  twice(_ + 1, 5)

  // 贷出模式:loan pattern
  def withPrintWriter(file: File, op: PrintWriter => Unit) {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  withPrintWriter(
    new File("date.txt"),
    writer => writer.println(new java.util.Date)
  )

  // 大括号和小括号. 大括号只有一个参数时才可以用
  println("Hello, world!")
  println{"Hello, world!"}

  val g = "Hello, world!"
  //g.substring { 7, 9 }
  g.substring(7, 9)


  // 使用贷出模式写文件, curry
  def withPrintWriter2(file: File)(op: PrintWriter => Unit) {
    val writer = new PrintWriter(file)
    try {
      op(writer)
    } finally {
      writer.close()
    }
  }

  // 现在参数只有一个了
  val file = new File("date.txt")
  withPrintWriter2(file) {
    writer => writer.println(new java.util.Date)
  }

  // 9.5 by-name parameter
  var assertionsEnabled = true
  def myAssert(predicate: () => Boolean) =
    if (assertionsEnabled && !predicate())
      throw new AssertionError

  myAssert(() => 5 > 3)

  def byNameAssert(predicate: => Boolean) =
    if (assertionsEnabled && !predicate)
      throw new AssertionError

  byNameAssert(5 > 3)

  def boolAssert(predicate: Boolean) =
    if (assertionsEnabled && !predicate)
      throw new AssertionError

  boolAssert(5 > 3)
}
