package com.zqh.akka.essentials.typedactor.calculator

import akka.actor.{ActorRef, ActorSystem, TypedActor, TypedProps}

object CalculatorActorSystem2 {
  def main(args: Array[String]) {

    val _system = ActorSystem("TypedActorsExample")

    val calculator1: CalculatorInt =
      TypedActor(_system).typedActorOf(TypedProps[Calculator]())

    val calculator2: CalculatorInt =
      TypedActor(_system).typedActorOf(TypedProps[Calculator]())

    // Create a router with Typed Actors
    val actor1: ActorRef = TypedActor(_system).getActorRefFor(calculator1)
    val actor2: ActorRef = TypedActor(_system).getActorRefFor(calculator2)

    val routees = Vector[ActorRef](actor1, actor2)
    /*
    val router = _system.actorOf(new Props().withRouter(
      BroadcastRouter(routees = routees)))

    router ! "Hello there"
    */
    _system.shutdown()
  }
}