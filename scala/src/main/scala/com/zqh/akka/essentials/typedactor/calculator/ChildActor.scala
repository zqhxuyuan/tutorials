package com.zqh.akka.essentials.typedactor.calculator

import akka.actor.{Actor, ActorLogging, actorRef2Scala}

class ChildActor extends Actor with ActorLogging {

  override def preStart() {
    log.info("Child Actor Started > {}", self.path)
  }

  def receive: Receive = {
    case message: String => throw new IllegalArgumentException("boom!")
    case message: Integer => sender ! message * message
    case _ => unhandled()
  }

  override def postStop() {
    log.info("Child Actor Stopped > {}", self.path)
  }
}