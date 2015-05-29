package com.zqh.scala.pis

import java.io.FileReader
import java.io.FileNotFoundException
import java.io.IOException
import java.net.URL
import java.net.MalformedURLException

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch07_control {

  def main(args: Array[String]) {
    // ***** 7.1 if *****
    var filename = "default.txt"
    if (!args.isEmpty)
      filename = args(0)

    // 在 Scala 里根据条件做初始化的惯例
    val filename2 =
      if (!args.isEmpty) args(0)
      else "default.txt"

    println(if (!args.isEmpty) args(0) else "default.txt")

    // ***** 7.2 while *****
    // 用 while 循环计算最大公约数
    def gcdLoop(x: Long, y: Long): Long = {
      var a = x
      var b = y
      while (a != 0) {
        val temp = a
        a = b % a
        b = temp
      }
      b
    }

    // 用 do-while 从标准输入读取信息
    var line = ""
    do {
      line = readLine()
      println("Read: " + line)
    } while (line != null)

    // 使用递归计算最大公约数
    def gcd(x: Long, y: Long): Long =
      if (y == 0) x else gcd(y, x % y)

    // ***** 7.3 for *****
    // 用 for 循环列表目录中的文件
    val filesHere = (new java.io.File(".")).listFiles
    for (file <- filesHere)
      println(file)

    for (i <- 1 to 4)
      println("Iteration " + i)

    for (i <- 1 until 4)
      println("Iteration " + i)

    // 用带过滤器的 for 发现.scala 文件
    val filesHere2 = (new java.io.File(".")).listFiles
    for (file <- filesHere2 if file.getName.endsWith(".scala"))
      println(file)

    for (file <- filesHere)
      if (file.getName.endsWith(".scala"))
        println(file)

    // 在 for 表达式中使用多个过滤器
    for (
      file <- filesHere
      if file.isFile;
      if file.getName.endsWith(".scala")
    ) println(file)

    for {
      file <- filesHere
      if file.isFile
      if file.getName.endsWith(".scala")
    } println(file)

    // 在 for 表达式中使用多个发生器
    def fileLines(file: java.io.File) = scala.io.Source.fromFile(file).getLines.toList

    def grep(pattern: String) =
      for {
        file <- filesHere
        if file.getName.endsWith(".scala")
        line <- fileLines(file)
        if line.trim.matches(pattern)
      } println(file + ": " + line.trim)
    grep(".*gcd.*")

    // 在 for 表达式里的流间赋值
    def grep2(pattern: String) =
      for {
        file <- filesHere
        if file.getName.endsWith(".scala")
        line <- fileLines(file)
        trimmed = line.trim
        if trimmed.matches(pattern)
      } println(file + ": " + trimmed)
    grep2(".*gcd.*")

    // 制造新集合
    def scalaFiles =
      for {
        file <- filesHere
        if file.getName.endsWith(".scala")
      } yield file

    // 用 for 把 Array[File]转换为 Array[Int]
    val forLineLengths =
      for {
        file <- filesHere
        if file.getName.endsWith(".scala")
        line <- fileLines(file)
        trimmed = line.trim
        if trimmed.matches(".*for.*")
      } yield trimmed.length

    // ***** 7.4 try-catch-exception *****
    val n = 99
    val half =
      if (n % 2 == 0)
        n / 2
      else
        throw new RuntimeException("n must be even")

    // Scala 的 try-catch 子句
    val fr : FileReader = new FileReader("input.txt")
    try {
      // Use and close file
    } catch {
      case ex: FileNotFoundException => // Handle missing file
      case ex: IOException => // Handle other I/O error
    } finally {
      fr.close()
    }

    def urlFor(path: String) =
      try {
        new URL(path)
      } catch {
        case e: MalformedURLException =>
          new URL("http://www.scalalang.org")
      }

    // ***** 7.5 match *****
    // 有副作用的 match 表达式
    val firstArg = if (args.length > 0) args(0) else ""
    firstArg match {
      case "salt" => println("pepper")
      case "chips" => println("salsa")
      case "eggs" => println("bacon")
      case _ => println("huh?")
    }

    // 生成值的 match 表达式
    val firstArg2 = if (!args.isEmpty) args(0) else ""
    val friend =
      firstArg2 match {
        case "salt" => "pepper"
        case "chips" => "salsa"
        case "eggs" => "bacon"
        case _ => "huh?"
      }
    println(friend)

    // java的break,continue
    /*
    int i = 0;
    boolean foundIt = false;
    while (i < args.length) {
      if (args[i].startsWith("-")) {
        i = i + 1;
        continue;
      }
      if (args[i].endsWith(".scala")) {
        foundIt = true;
        break;
      }
      i = i + 1;
    }
    */

    // 不带 break 或 continue 的循环
    var i = 0
    var foundIt = false
    while (i < args.length && !foundIt) {
      if (!args(i).startsWith(""))
      {
        if (args(i).endsWith(".scala"))
          foundIt = true
      }
      i = i + 1
    }

    // 不用 var 做循环的递归替代方法
    def searchFrom(i: Int): Int =
      if (i >= args.length) -1// 不要越过最后一个参数
      else if (args(i).startsWith("-")) searchFrom(i + 1)// 跳过选项
      else if (args(i).endsWith(".scala")) i // 找到!
      else searchFrom(i + 1) // 继续找
    val j = searchFrom(0)

    // ***** 7.7 变量范围 *****
    // 打印乘法表时的变量范围
    def printMultiTable() {
      var i = 1
      while (i <= 10) {
        var j = 1
        while (j <= 10) {
          val prod = (i * j).toString
          var k = prod.length
          while (k < 4) {
            print(" ")
            k += 1
          }
          print(prod)
          j += 1
        }
        println()
        i += 1
      }
    }

    // ***** 7.8 重构指令式风格的代码 *****
    // 创建乘法表的函数式方法
    // 以序列形式返回一行乘法表
    def makeRowSeq(row: Int) =
      for (col <- 1 to 10) yield {
        val prod = (row * col).toString
        val padding = " " * (4 - prod.length)
        padding + prod
      }
    // 以字串形式返回一行乘法表
    def makeRow(row: Int) = makeRowSeq(row).mkString
    // 以字串形式返回乘法表,每行记录占一行字串
    def multiTable() = {
      val tableSeq = // 行记录字串的序列
        for (row <- 1 to 10)
        yield makeRow(row)
      tableSeq.mkString("\n")
    }

  }
}
