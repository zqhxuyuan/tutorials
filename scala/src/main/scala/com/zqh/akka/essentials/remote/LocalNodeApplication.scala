package com.zqh.akka.essentials.remote

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * Created by hadoop on 15-2-27.
 */
object LocalNodeApplication {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("LocalNodeApp", ConfigFactory.load().getConfig("LocalSys"))
    val clientActor = system.actorOf(Props[LocalActor])
    clientActor ! "Hello"
    Thread.sleep(4000)
    system.shutdown()
  }
}

class LocalActor extends Actor with ActorLogging {
  val remoteActor = context.actorFor("akka://RemoteNodeApp@localhost:2552/user/remoteActor")
  implicit val timeout = Timeout(5 seconds)
  def receive: Receive = {
    case message: String =>
      val future = (remoteActor ? message).mapTo[String]
      val result = Await.result(future, timeout.duration)
      log.info("Message received from Server -> {}", result)
  }
}