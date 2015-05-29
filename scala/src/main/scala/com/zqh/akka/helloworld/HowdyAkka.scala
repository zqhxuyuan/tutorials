package com.zqh.akka.helloworld

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

/**
 * Created by hadoop on 15-2-9.
 *
 * http://www.reactive.io/tips/2014/03/28/getting-started-with-actor-based-programming-using-scala-and-akka/
 * https://github.com/reactive-io/howdy-akka-tip
 */
object HowdyAkka extends App {
  case class Ticket(quantity: Int)
  case class FullPint(number: Int)
  case class EmptyPint(number: Int)


  class BarTender extends Actor with ActorLogging {
    var total = 0

    def receive = {
      case Ticket(quantity) =>
        total = total + quantity

        log.info(s"I'll get $quantity pints for [${sender.path}]")

        for (number <- 1 to quantity) {
          log.info(s"Pint $number is coming right up for [${sender.path}]")

          Thread.sleep(1000)

          log.info(s"Pint $number is ready, here you go [${sender.path}]")

          sender ! FullPint(number)
        }

      case EmptyPint(number) =>
        total match {
          case 1 =>
            log.info("Ya'll drank those pints quick, time to close up shop")

            context.system.shutdown()

          case n =>
            total = total - 1

            log.info(s"You drank pint $number quick, but there are still $total pints left")
        }
    }
  }

  class Person extends Actor with ActorLogging {
    def receive = {
      case FullPint(number) =>
        log.info(s"I'll make short work of pint $number")

        Thread.sleep(1000)

        log.info(s"Done, here is the empty glass for pint $number")

        sender ! EmptyPint(number)
    }
  }


  // main method
  val system = ActorSystem("howdy-akka")

  val zed = system.actorOf(Props(new BarTender), "zed")

  val alice   = system.actorOf(Props(new Person), "alice")
  val bob     = system.actorOf(Props(new Person), "bob")
  val charlie = system.actorOf(Props(new Person), "charlie")

  zed.tell(Ticket(2), alice)
  zed.tell(Ticket(3), bob)
  zed.tell(Ticket(1), charlie)

  system.awaitTermination()
}

