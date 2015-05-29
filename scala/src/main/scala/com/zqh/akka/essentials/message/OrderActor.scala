package com.zqh.akka.essentials.message

import akka.actor.Actor

class OrderActor extends Actor {

  def receive = {
    case userId: Int => sender ! new Order(userId, 123, 345, 5)
  }
}