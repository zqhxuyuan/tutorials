package example

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
 * Created by zhengqh on 15/11/11.
 */
object FutureTest {

  def main(args: Array[String]) {

    val aFuture = Future{
      println("abc")
      "abc"
    }

    try {
      val result = Await.result(aFuture, 1 seconds)
      println("result:"+result)
    }catch {
      case _ => {
        println("TimeOUT")
      }
    }
  }

  def simple(): Unit ={
    val fileName = "afdaf-Data.db"
    val fileOfGroup = fileName.substring(0, fileName.indexOf("Data"))
    println(fileOfGroup)

    val date = new java.util.Date();
    val format = new java.text.SimpleDateFormat("yyyyMMddHHmm");
    val str = format.format(date)
    println(str)
  }

  def mapFutures(): Unit ={
    val start = System.currentTimeMillis()
    val strs = List("a","b","c")
    val futures = strs.map(str=>Future{
      //Thread.sleep(5000)
      str
    })
    val end = System.currentTimeMillis()
    println("cost(ms):" + (end - start))

    val future = Future.sequence(futures)
    try {
      val result = Await.result(future, 1 seconds)
      println("result:"+result)
    }catch {
      case _ => {
        println("TimeOUT")
      }
    }

    val end2 = System.currentTimeMillis()
    println("cost(ms):"+(end2 - end))
  }

}
