package com.zqh.akka.helloworld

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}

object SimpleAkka extends App{
  // Msg : 使用case class表示一个不可变,可序列化(可在网络中传输)的消息对象
  case class Greeting(who: String)

  // Actor: 继承Actor trait,并实现receive方法
  class GreetingActor extends Actor with ActorLogging {
    // 4.this Actor receive a msg from client api
    // 当一个消息发送给Actor时，它的receive方法会被（Akka）调用
    def receive = {
      // 5.the msg is kind of Greeting, do sth
      // 使用模式匹配来识别消息类型并作出响应(相比回调实现更加简洁和优雅)
      case Greeting(who) => log.info("Hello " + who)
      case _ => log.info("nop")
    }
  }

  // Main : 创建一个system
  val system = ActorSystem("MySystem")
  // 1.initialize an Actor : 使用system创建一个Actor
  val greeter = system.actorOf(Props[GreetingActor], name = "greeter")

  // 2.create a msg: Greeting 创建一条消息
  // 3.send this msg to Actor 发送消息给Actor
  greeter ! Greeting("Charlie Parker")

  system.shutdown()
}






