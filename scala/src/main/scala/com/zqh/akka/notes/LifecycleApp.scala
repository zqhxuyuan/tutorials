package com.zqh.akka.notes

import akka.actor.{Props, ActorSystem, ActorLogging, Actor}
import akka.event.LoggingReceive

class BasicLifecycleLoggingActor extends Actor with ActorLogging{

  log.info ("Inside BasicLifecycleLoggingActor Constructor")
  log.info (context.self.toString())

  override def preStart() ={
    log.info("Inside the preStart method of BasicLifecycleLoggingActor")
  }

  def receive = LoggingReceive{
    case "hello" => log.info ("hello")
  }

  override def postStop()={
    log.info ("Inside postStop method of BasicLifecycleLoggingActor")
  }

}

object LifecycleApp extends App{

  val actorSystem = ActorSystem("LifecycleActorSystem")
  val lifecycleActor = actorSystem.actorOf(Props[BasicLifecycleLoggingActor],"lifecycleActor")

  lifecycleActor ! "hello"

  //wait for a couple of seconds before shutdown
  Thread.sleep(2000)
  actorSystem.shutdown()

}