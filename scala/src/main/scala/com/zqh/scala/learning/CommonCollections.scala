package com.zqh.scala.learning

/**
 * Created by zhengqh on 15/10/18.
 */
object CommonCollections{

  def main(args: Array[String]) {
    simpleTest()
  }

  def simpleTest(): Unit ={
    val validations = List(true, true, false, true, true, true)

    //不包含false: 每个都是true
    val val1 = !(validations.contains(false))
    val val2 = validations.forall(_ == true)
    //存在一个false都不行!
    val val3 = validations.exists(_ == false) == false

    //使用循环的方式判断指定的元素是否存在列表中
    val included = contains(19, List(46, 19, 92))

    val included2 = boolReduce(List(46, 19, 92), false) {
      //f: (Boolean, Int) => Boolean, 即f(a, i)
      //如果a=true, 返回true, 对应boolReduce中则a=f(a,i)=true
      //如果a=false(初始值a就是false), a=f(a,i), a=(i==19), 其中i是列表中的每个元素

      //假设列表第一个元素时,boolReduce中的f(a,i)=f(start,i)=f(false,46)
      //对应这里a=false, i=46. 因为a=false, 所以返回值=(i==19)=(46==19)=false
      //然后再回到boolReduce中a=f(a,i)=false. 这个false就是上面的返回值.
      //接着判断列表的第二个元素, a=f(a,i)=f(false,19)
      //对应这里a=false,i=19, 返回值=(i==19)=(19==19)=true
      //所以在boolReduce中a=f(a,i)=true!
      (a, i) => if(a) a else (i == 19)
    }

    val forall = boolReduce(List(46, 19, 92), false) {
      (a, i) => if(a) a else (i > 10)
    }

    val include3 = reduceOp(List(46, 19, 92), false) {
      (a, i) => if(a) a else (i == 19)
    }

    val answer = reduceOp(List(11.3, 23.5, 7.2), 0.0)(_ + _)

    val include4 = List(46, 19, 92).foldLeft(false) {
      (a, i) => if(a) a else (i > 10)
    }

    //uses the first element in the list for a starting value instead of taking it as a parameter
    val answer2 = List(11.3, 23.5, 7.2).reduceLeft(_ + _)
  }

  def contains(x: Int, l: List[Int]): Boolean ={
    //判断某个元素是否存在于列表中, 初始值为不存在.
    var a : Boolean = false
    for(i <- l)
      //循环列表每个元素, 判断每个元素是否和指定的x相等, 并更新a的值
      //只有有一个元素和x相等, a=true. 虽然还会再循环列表剩余的元素,但是这个时候都不会再更新a的值了
      if(!a) a = (i == x)
    a
  }

  //传入函数, 这样可以实现的不仅仅是contains,还可以是exists, forall, startsWith等布尔操作
  def boolReduce(l: List[Int], start: Boolean)
                (f: (Boolean, Int) => Boolean): Boolean = {
    var a = start
    for(i <- l)
      //将如何更新的操作,交给调用者自己来确定.
      //更新操作是一个函数, 返回值为更新后的a的值, 第二个参数是列表中的每个元素!
      //这里传递的参数会传给调用者自己实现的函数!
      a = f(a, i)
    a
  }


  def reduceOp[A,B](l : List[A], start : B)
                   (f : (B, A) => B) : B = {
    var a = start
    for(i <- l) a = f(a, i)
    a
  }

}
