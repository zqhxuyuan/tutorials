package com.zqh.akka.effective

import akka.actor.SupervisorStrategy._
import akka.actor.{Actor, OneForOneStrategy}

case object Start

class StuntDouble extends Actor {
  def receive = {
    case _ =>
  }
}

class StuntDoubleSupervisor extends Actor {
  override val supervisorStrategy = OneForOneStrategy() {
    case _ => Restart
  }

  def receive = {
    case Start =>
  }
}