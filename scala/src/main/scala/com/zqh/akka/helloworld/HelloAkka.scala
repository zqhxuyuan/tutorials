package com.zqh.akka.helloworld

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

/**
 * Output:
 * [MySystem-akka.actor.default-dispatcher-5] [akka://MySystem/user/greeter] Hello Charlie Parker
 */
object HelloAkka extends App{
  // Msg
  case class Greeting(who: String)

  // Actor
  class GreetingActor extends Actor with ActorLogging {
    // this Actor receive a msg from client api
    def receive = {
      // the msg is kind of Greeting, do sth
      case Greeting(who) ⇒ log.info("Hello " + who)
    }
  }

  // Main
  val system = ActorSystem("MySystem")
  // initialize an Actor
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
  // 1. create a msg: Greeting
  // 2. send this msg to Actor
  greeter ! Greeting("Charlie Parker")

  system.shutdown()
}






