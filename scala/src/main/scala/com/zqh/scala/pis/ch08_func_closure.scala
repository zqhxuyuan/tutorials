package com.zqh.scala.pis

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch08_func_closure {

  import scala.io.Source
  object LongLines {

    // 8.1 带私有的 processLine 方法
    def processFile(filename: String, width: Int) {
      val source = Source.fromFile(filename)
      for (line <- source.getLines)
        processLine(filename, width, line)
    }
    private def processLine(filename:String, width:Int, line:String) {
      if (line.length > width)
        println(filename+": "+line.trim)
    }

    // 8.2 带本地 processLine 方法
    def processFile2(filename: String, width: Int) {
      def processLine(line: String) {
        if (line.length > width)
          print(filename +": "+ line)
      }
      val source = Source.fromFile(filename)
      for (line <- source.getLines)
        processLine(line)
    }
  }

  // 8.3 函数是第一类值
  var increase = (x: Int) => x + 1
  increase(10)

  increase = (x: Int) => x + 9999
  increase(10)

  increase = (x: Int) => {
    println("We")
    println("are")
    println("here!")
    x + 1
  }
  increase(10)

  val someNumbers = List(-11, -10, -5, 0, 5, 10)
  someNumbers.foreach((x: Int) => println(x))
  someNumbers.filter((x: Int) => x > 0)

  // 8.4 函数文本的短格式
  someNumbers.filter((x) => x > 0)
  someNumbers.filter(x => x > 0)

  // 8.5 占位符语法
  someNumbers.filter(_ > 0)

  val f = (_: Int) + (_: Int)
  f(5, 10)

  // 8.6 偏应用函数
  // 使用一个下划线替换整个参数列表
  someNumbers.foreach(println _)

  def sum(a: Int, b: Int, c: Int) = a + b + c
  sum(1, 2, 3)

  val a = sum _
  a(1, 2, 3)
  a.apply(1, 2, 3)

  val b = sum(1, _: Int, 3)
  b(2)
  b(5)

  // 8.7 闭包
  var more = 1
  val addMore = (x: Int) => x + more
  addMore(10)

  more = 9999
  addMore(10)

  val someNumbers2 = List(-11, -10, -5, 0, 5, 10)
  var sum2 = 0
  someNumbers.foreach(sum2 += _)
  println(sum2)

  def makeIncreaser(more: Int) = (x: Int) => x + more
  val inc1 = makeIncreaser(1)
  val inc9999 = makeIncreaser(9999)
  inc1(10)
  inc9999(10)

  // 8.8 重复参数
  def echo(args: String*) =
    for (arg <- args) println(arg)

  echo()
  echo("one")
  echo("hello", "world!")

  val arr = Array("What's", "up", "doc?")
  echo(arr: _*)

  // 8.9 尾递归
  def approximate(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else approximate(improve(guess))

  def approximateLoop(initialGuess: Double): Double = {
    var guess = initialGuess
    while (!isGoodEnough(guess))
      guess = improve(guess)
    guess
  }

  val target = 50
  def isGoodEnough(res : Double) : Boolean ={
    if(res != target) false else true
  }
  def improve(res : Double) : Double = {
    if(res - target > target / 2) res + target else res
  }

  // 不是尾递归, 因为在递归调用之后执行了递增操作
  def boom(x: Int): Int =
    if (x == 0) throw new Exception("boom!")
    else boom(x - 1) + 1

  // 下面才是
  def bang(x: Int): Int =
    if (x == 0) throw new Exception("bang!")
    else bang(x+1)

  // 尾递归的局限
  def isEven(x: Int): Boolean =
    if (x == 0) true else isOdd(x - 1)
  def isOdd(x: Int): Boolean =
    if (x == 0) false else isEven(x - 1)

  val funValue = nestedFun _
  def nestedFun(x: Int) {
    if (x != 0) { println(x); funValue(x - 1) }
  }
}
