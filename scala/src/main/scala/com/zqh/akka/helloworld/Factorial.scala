package com.zqh.akka.helloworld

/**
 * Created by hadoop on 15-2-9.
 *
 * http://www.reactive.io/tips/2014/04/08/parallel-computing-made-easy-with-scala-and-akka/
 * https://github.com/reactive-io/factorial-tip
 */
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

import scala.annotation.tailrec

object Factorial extends App {
  val factorials = List(20, 18, 20, 22, 20, 19, 20, 21)

  val system = ActorSystem("factorial")

  val collector = system.actorOf(Props(new FactorialCollector(factorials)), "collector")

  class FactorialCollector(factorials: List[Int]) extends Actor with ActorLogging {
    var list: List[BigInt] = Nil
    var size = factorials.size

    for (num <- factorials) {
      context.actorOf(Props(new FactorialCalculator)) ! num
    }

    def receive = {
      case (num: Int, fac: BigInt) => {
        log.info(s"factorial for $num")

        list = num :: list
        size -= 1

        if (size == 0) {
          context.system.shutdown()
        }
      }
    }
  }

  class FactorialCalculator extends Actor {
    def receive = {
      case num: Int => sender ! (num, factor(num))
    }

    private def factor(num: Int) = factorTail(num, 1)

    @tailrec private def factorTail(num: Int, acc: BigInt): BigInt = {
      (num, acc) match {
        case (0, a) => a
        case (n, a) => factorTail(n - 1, n * a)
      }
    }
  }
}

