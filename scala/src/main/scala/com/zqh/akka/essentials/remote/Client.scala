package com.zqh.akka.essentials.remote

import akka.actor._
import akka.remote.RemoteScope
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.Await
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory

/**
 * Created by hadoop on 15-2-27.
 *
 * How to Run?
 *
 * You Should not run Server, just run this Client class. or else u'll get Caused by: java.net.BindException: 地址已在使用
 */
object Client {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("WorkerSys", ConfigFactory.load().getConfig("ClientSys"))
    val clientActor = system.actorOf(Props[ClientActor], name = "clientActor")
    clientActor ! "Hi there"
    Thread.sleep(4000)
    system.shutdown()
  }
}

class ClientActor extends Actor with ActorLogging {

  /**
   * Use one of the Options to get a reference to server actor
   */
  //First option - Get a reference to the remote actor
  //var serverActor = context.actorFor("akka://ServerSys@127.0.0.1:2552/user/serverActor")

  // Second option - create remote Actor instance
  //val addr = Address("akka", "ServerSys", "127.0.0.1", 2552)
  //serverActor = context.actorOf(Props[ServerActor].withDeploy(Deploy(scope = RemoteScope(addr))))

  //Third option - creating the actor using the actor name defined via application.conf
  var serverActor = context.actorOf(Props[ServerActor], name = "remoteServerActor")

  def receive: Receive = {
    case message: String =>
      implicit val timeout = Timeout(5 seconds)
      val future = (serverActor ? message).mapTo[String]
      val result = Await.result(future, timeout.duration)
      log.info("Message received from Server -> {}", result)
  }
}
