package com.zqh.scala.pis

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch06_func_obj {

  def main(args: Array[String]) {
    val oneHalf = new Rational(1, 2)
    val twoThirds = new Rational(2, 3)
    oneHalf add twoThirds

    val r = new Rational(1, 2)
    r.numer
    r.denom

    val r2 = new Rational(3)

    val x = new Rational(2, 3)
    x * x
    x * 2

    implicit def intToRational(x: Int) = new Rational(x)
    2 * x

    val y = new Rational(2, 3)
    x + y
    x.+(y)

    x + x * y
    (x + x) * y
    x + (x * y)


  }

  class Rational(n: Int, d: Int) {
    // 检查先决条件
    require(d != 0)

    // 带私有字段和方法
    private val g = gcd(n.abs, d.abs)

    // 带字段
    val numer: Int = n / g
    val denom: Int = d / g

    // 带有从构造器
    def this(n: Int) = this(n, 1)

    // 重新实现 toString 方法
    override def toString = numer+"/"+denom

    def add(that: Rational): Rational =
      new Rational(
        numer * that.denom + that.numer * denom,
        denom * that.denom
      )

    // this
    def lessThan(that: Rational) =
      this.numer * that.denom < that.numer * this.denom

    def max(that: Rational) =
      if (this.lessThan(that)) that else this

    private def gcd(a: Int, b: Int): Int =
      if (b == 0) a else gcd(b, a % b)

    // 带操作符方法
    def +(that: Rational): Rational =
      new Rational(
        numer * that.denom + that.numer * denom,
        denom * that.denom
      )
    def *(that: Rational): Rational =
      new Rational(numer * that.numer, denom * that.denom)

    // 含有重载方法
    def +(i: Int): Rational =
      new Rational(numer + i * denom, denom)
    def -(that: Rational): Rational =
      new Rational(
        numer * that.denom - that.numer * denom,
        denom * that.denom
      )
    def -(i: Int): Rational =
      new Rational(numer - i* denom, denom)
    def *(i: Int): Rational =
      new Rational(numer * i, denom)
    def /(that: Rational): Rational =
      new Rational(numer * that.denom, denom * that.numer)
    def /(i: Int): Rational =
      new Rational(numer, denom * i)
  }

}
