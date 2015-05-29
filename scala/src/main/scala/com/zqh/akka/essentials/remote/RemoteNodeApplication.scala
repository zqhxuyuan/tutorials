package com.zqh.akka.essentials.remote

import akka.actor.{Actor, Props, ActorSystem}
import akka.kernel.Bootable
import com.typesafe.config.ConfigFactory

/**
 * Created by hadoop on 15-2-27.
 */
class RemoteNodeApplication extends Bootable {
  val system = ActorSystem("RemoteNodeApp", ConfigFactory.load().getConfig("RemoteSys"))

  def startup = {
    system.actorOf(Props[RemoteActor], name = "remoteActor")
  }

  def shutdown = {
    system.shutdown()
  }
}

object RemoteNodeApplication extends App {
  val system = ActorSystem("RemoteNodeApp", ConfigFactory.load().getConfig("RemoteSys"))
  system.actorOf(Props[RemoteActor], name = "remoteActor")
  //system.shutdown()
}

class RemoteActor extends Actor {
  def receive: Receive = {
    case message: String =>
      // Get reference to the message sender and reply back
      sender ! message + " got something"
  }
}
