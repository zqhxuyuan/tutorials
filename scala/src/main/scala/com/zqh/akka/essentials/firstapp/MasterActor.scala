package com.zqh.akka.essentials.firstapp

import akka.actor.{Actor, ActorRef, Props, actorRef2Scala}
import com.zqh.akka.essentials.firstapp.Result

class MasterActor extends Actor {

  val aggregateActor: ActorRef = context.actorOf(Props[AggregateActor], name = "aggregate")
  val reduceActor: ActorRef = context.actorOf(Props(new ReduceActor(aggregateActor)), name = "reduce")
  val mapActor: ActorRef = context.actorOf(Props(new MapActor(reduceActor)), name = "map")

  def receive: Receive = {
    case message: String =>
      mapActor ! message
    case message: Result =>
      aggregateActor ! message
  }
}