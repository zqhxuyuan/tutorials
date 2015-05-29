package com.zqh.akka.essentials.stm.stockticker

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.agent.Agent
import java.lang.Float

object StockApplication {

  def main(args: Array[String]): Unit = {
    val _system = ActorSystem("Agent-example")
    //val stock = new Stock("APPL", Agent(new Float("600.45"))(_system))
    val stock = new Stock("APPL", Agent(new Float("600.45")))

    val readerThreads = new Array[Thread](10)
    val updateThreads = new Array[Thread](10)

    for (i <- 0 until readerThreads.length) {
      readerThreads(i) = new Thread(new StockReader(stock))
      readerThreads(i).setName("#" + i)
    }
    for (i <- 0 until updateThreads.length) {
      updateThreads(i) = new Thread(new StockUpdater(stock))
      updateThreads(i).setName("#" + i)
    }

    for (i <- 0 until readerThreads.length)
      readerThreads(i).start()

    for (i <- 0 until updateThreads.length)
      updateThreads(i).start()

    Thread.sleep(3000)
    _system.shutdown()
  }

  case class Stock(symbol: String, price: Agent[Float])


  class StockReader(stock: Stock) extends Runnable {
    var countDown = 10

    override def run(): Unit = {
      while (countDown > 0) {
        Thread.sleep(51)
        val x: String = Thread.currentThread().getName
        val stockTicker = stock.price.get()
        println("Quote read by thread (" + x + "), current price " + stockTicker)
        countDown = countDown - 1
      }
    }
  }

  class StockUpdater(stock: Stock) extends Runnable {
    var countDown = 5

    override def run(): Unit = {
      while (countDown > 0) {
        Thread.sleep(55)
        val x: String = Thread.currentThread().getName
        stock.price.send(_ + 10)
        println("Quote update by thread (" + x + "), current price " + stock.price.get)
        countDown = countDown - 1
      }
    }
  }
}
