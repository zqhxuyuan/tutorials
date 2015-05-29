package com.zqh.scala.pis

/**
 * Created by zqhxuyuan on 15-2-28.
 */
object ch10_compo_extend {

  def main(args: Array[String]) {
    val ae = new ArrayElement(Array("hello", "world"))
    println(ae.width)
    println(ae.height)
    ae.contents.foreach(println)

    val e: Element = new ArrayElement(Array("hello"))

    class Cat {
      val dangerous = false
    }
    class Tiger(override val dangerous: Boolean,
                private var age: Int
                 ) extends Cat


    val e1: Element = new ArrayElement(Array("hello", "world"))
    val le: ArrayElement = new LineElement("hello")
    val e2: Element = le
    val e3: Element = new UniformElement('x', 2, 3)
  }

  // 定义抽象方法和类
  abstract class Element {
    def contents: Array[String]
    def height: Int = contents.length
    def width: Int = if (height == 0) 0 else contents(0).length

    def above(that: Element): Element =
      new ArrayElement(this.contents ++ that.contents)

    def beside0(that: Element): Element = {
      val contents = new Array[String](this.contents.length)
      for (i <- 0 until this.contents.length)
        contents(i) = this.contents(i) + that.contents(i)
      new ArrayElement(contents)
    }

    def beside(that: Element): Element = {
      new ArrayElement(
        for ((line1, line2) <- this.contents zip that.contents)
          yield line1 + line2
      )
    }

    override def toString = contents mkString "\n"
  }

  class ArrayElement(conts: Array[String]) extends Element {
    // 重载了父类[抽象]成员的成员[不需要]override修饰符
    val contents: Array[String] = conts
  }

  // 定义 contents 为参数化字段
  class ArrayElement2(val contents: Array[String]) extends Element

  class LineElement(s: String) extends ArrayElement(Array(s)) {
    // 所有重载了父类[具体]成员的成员[都需要]override修饰符
    override def width = s.length
    override def height = 1
  }

  class LineElement2(s: String) extends Element {
    val contents = Array(s)
    override def width = s.length
    override def height = 1
  }

  class UniformElement(
                        ch: Char,
                        override val width: Int,
                        override val height: Int
                        ) extends Element {
    private val line = ch.toString * width
    def contents = Array(height.toString + line, line)
  }

  //伴生对象
  object Element {
    def elem(contents: Array[String]): Element = new ArrayElement(contents)
    def elem(chr: Char, width: Int, height: Int): Element = new UniformElement(chr, width, height)
    def elem(line: String): Element = new LineElement(line)
  }

  // 重构以使用工厂方法
  import Element.elem
  abstract class Element2 {
    def contents: Array[String]
    def width: Int = if (height == 0) 0 else contents(0).length
    def height: Int = contents.length

    def above(that: Element): Element = elem(this.contents ++ that.contents)
    def beside(that: Element): Element = elem(for ((line1, line2) <- this.contents zip that.contents) yield line1 + line2)

    override def toString = contents mkString "\n"
  }

  // 用私有类隐藏实现
  object Element2 {
    private class ArrayElement(val contents: Array[String]) extends Element
    private class LineElement(s: String) extends Element {
      val contents = Array(s)
      override def width = s.length
      override def height = 1
    }
    private class UniformElement(ch: Char, override val width: Int, override val height: Int) extends Element {
      private val line = ch.toString * width
      def contents = Array(line)
    }

    def elem(contents: Array[String]): Element = new ArrayElement(contents)
    def elem(chr: Char, width: Int, height: Int): Element = new UniformElement(chr, width, height)
    def elem(line: String): Element = new LineElement(line)
  }
}
