/**
 * Created by zhengqh on 15/10/14.
 *
 * https://twitter.github.io/scala_school/zh_cn/advanced-types.html
 */
object AdvancedType {

  def viewBound(): Unit ={
    //使用类型边界[已经被废弃,不建议]
    def foo[T <% Int](x: T):Int = x

    //view bounds are deprecated, its better replace with implicit.
    //或者使用隐式自动类型转换[建议的方式]
    implicit def a[T](n:T) = n match {
      case x:String => x.toInt
    }

    //类型边界的调用方式
    foo("123")

    //自动类型转换
    val y : Int = "123"
  }

  def contextBound(): Unit ={
    //转换为context bound, we need to implicitly have something that does T => Int
    type L[X] = X => Int

    def goo[T : L](x: T):Int = x

    goo(123)
    //goo("123")

    def goo2[T](x: T)(implicit ev: T => Int):Int = x
    goo2(123)
    //goo2("123")

    //上面几种调用方式只能传递Int类型, 下面则可以是String类型
    def goo3[T, E](x: T)(implicit ev: T => E):E = x
    goo3("123")
  }

  def implicitFunc(): Unit ={
    //不需要指定一个类型是等/子/超于另一个类，你可以通过转换这个类来伪装这种关联关系
    //参数是String类型, 任何String类型都可以自动被转换为Int类型
    implicit def strToInt(x: String) = x.toInt

    //一个约束是定义了多个方法,转换后的类型如果有多个,那么怎么知道要用那个类型?
    //上面因为String已经被隐式转换为Int,就不能再定义转换为其他类型了,IDE会报错
    //这就好比上面的viewBound方法,case x:String,你不能转换为多个类型! 只能选择其一.
    //implicit def strToDouble(x: String) = x.toFloat

    //就好像定义了一个方法strToInt, 字符串被作为参数传入: def strToInt(x: String)
    //如果没有添加implict关键字, 则调用方式为: strToInt("123")
    //由于添加了implicit关键字, 不需要使用方法调用的方式
    //由于没有了函数的调用, 因此函数的名称可以是任意的!
    val y: Int = "123"

    //现在我们可以把字符串看做是Int了!
    math.max("123", 111)
  }

  //视界，就像类型边界，要求对给定的类型存在这样一个函数。您可以使用<%指定类型限制
  def visibleType(): Unit ={

    //A 必须“可被视”为 Int
    class Container[A <% Int] {
      def addIt(x: A) = 123 + x
    }

    //TODO: No implicit view available from String => Int
    //(new Container[String]).addIt("123")

    (new Container[Int]).addIt(123)

    // error: could not find implicit value for evidence parameter of type (Float) => Int
    //(new Container[Float]).addIt(123.2F)
  }

  def implicitMethodParameterTest(){

    //List支持对数字内容执行sum，但对其他内容却不行。可是Scala的数字类型并不都共享一个超类，所以我们不能使用T <: Number
    //Scala的math库对适当的类型T 定义了一个隐含的Numeric[T]。 然后在List定义中使用它: sum[B >: A](implicit num: Numeric[B]): B
    //A定义在List上(实际上是TraversableOnce[A]), 我们传入的参数类型决定了A的类型, 如果是List(1,2)则A是Int类型.
    //B >: A, 表示类型B是类型A的超类/父类. 而A是Int,Double等类型, B就是Int,Double的超类, 那么具体是什么类型?
    List(1,2,3).sum  //B >: Int
    List(1.0, 2.0, 3.0).sum //B >: Double

    class Container[A](value: A) {
      //方法的implicit参数类型A必须是Int类型, 即value: A必须是Int类型
      def addIt(implicit evidence: A =:= Int) = 123 + value
    }

    (new Container(123)).addIt

    //error: could not find implicit value for parameter evidence: =:=[java.lang.String,Int]
    //(new Container("123")).addIt

    //和sum一样, min的implicit: def min[B >: A](implicit cmp: Ordering[B]): A
    //求和时必须确保List中的元素是数字, 求最小值时, 确保List中的元素是可排序的
    List(1,2,3,4).min
    List(1,2,3,4).min(new Ordering[Int] { def compare(a: Int, b: Int) = b compare a })

    //上下文边界和implicitly[]: 串联和访问隐式参数的快捷方式
    def foo[A](implicit x: Ordered[A]) {}
    def foo2[A : Ordered] {}
    implicitly[Ordering[Int]]
  }


  def main(args: Array[String]) {
    viewBound()


  }


}
