package example

/**
 * Created by zhengqh on 15/9/11.
 */
object FuncTest {

  def max = (a:Int, b:Int) => Math.max(a,b)
  val min = (a:Int, b:Int) => Math.min(a,b)

  def main(args: Array[String]) {
    //println(min(1,2))
    //println(max(1,2))

    cacluate(max, 1, 2)
  }

  def cacluate(func: (Int,Int) => Int, a:Int, b:Int): Unit ={
    val start = System.currentTimeMillis()
    func(a,b)
    val end = System.currentTimeMillis()
    println(end-start)
  }
}
