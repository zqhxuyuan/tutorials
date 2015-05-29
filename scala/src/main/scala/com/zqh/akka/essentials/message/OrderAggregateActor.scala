package com.zqh.akka.essentials.message

import akka.actor.Actor

class OrderAggregateActor extends Actor {
  def receive = {
    case orderHistory: OrderHistory => println(orderHistory.toString())
  }
}