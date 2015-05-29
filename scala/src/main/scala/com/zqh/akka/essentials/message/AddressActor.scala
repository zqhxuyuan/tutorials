package com.zqh.akka.essentials.message

import akka.actor.Actor

class AddressActor extends Actor {

  def receive = {
    case userId: Int => sender ! new Address(userId, "Munish Gupta", "Sarjapura Road", "Bangalore, India")
  }
}