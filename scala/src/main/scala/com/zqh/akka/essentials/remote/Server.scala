package com.zqh.akka.essentials.remote

import akka.actor.{Actor, Props, ActorSystem}
import com.typesafe.config.ConfigFactory

/**
 * Created by hadoop on 15-2-27.
 */
object Server {

  def main(args: Array[String]): Unit = {
    // load the configuration
    val config = ConfigFactory.load().getConfig("ServerSys")

    val system = ActorSystem("ServerSys", config)

    val serverActor = system.actorOf(Props[ServerActor], name = "serverActor")
  }
}

class ServerActor extends Actor {
  def receive: Receive = {
    case message: String =>
      // Get reference to the message sender and reply back
      sender ! message + " got something"
  }
}
