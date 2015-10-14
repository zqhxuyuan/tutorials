package com.zqh.scala

import scala.async.Async.{async, await}

import scala.concurrent.{Await, ExecutionContext, Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
/**
 * Created by zhengqh on 15/10/14.
 * https://github.com/scala/async
 */
object AsyncTest extends App{

  //async标记了异步执行的代码块. 这样的代码块通常包括了一个或多个await调用
  val future = async {
    val f1 = async {
      Thread.sleep(5000)
      true
    }
    val f2 = async {
      Thread.sleep(2000)
      42
    }
    //await调用:marks a point(标记一个点) at which the computation(计算会被展厅)
    //will be suspended until the awaited Future is complete(直到计算完成).
    if (await(f1))
      await(f2)
    else 0
  }

  def slowCalcFuture: Future[Int] = future                  // 01
  def combinedNonBlock: Future[Int] = async {               // 02
      await(slowCalcFuture) + await(slowCalcFuture)         // 03
  }

  val x: Int = Await.result(combinedNonBlock, 10.seconds)   // 05

  println(x)

  def combinedParallel: Future[Int] = async {
    val future1 = slowCalcFuture
    val future2 = slowCalcFuture
    await(future1) + await(future2)
  }
}
