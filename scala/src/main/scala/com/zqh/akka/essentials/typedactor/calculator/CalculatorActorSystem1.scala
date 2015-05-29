package com.zqh.akka.essentials.typedactor.calculator

import akka.actor.{ActorRef, ActorSystem, TypedActor, TypedProps, _}
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration._

object CalculatorActorSystem1 {

  def main(args: Array[String]) {

    val _system = ActorSystem("TypedActorsExample")

    val calculator: CalculatorInt =
      TypedActor(_system).typedActorOf(TypedProps[Calculator]())

    calculator.incrementCount()

    // Invoke the method and wait for result
    val future = calculator.add(14, 6)
    var result = Await.result(future, Timeout(5 second).duration)
    println("Result is " + result)

    // Invoke the method and wait for result
    var counterResult = calculator.incrementAndReturn()
    println("Result is " + counterResult.get)

    counterResult = calculator.incrementAndReturn()
    println("Result is " + counterResult.get)

    // Get access to the ActorRef
    val calActor: ActorRef = TypedActor(_system).getActorRefFor(calculator)
    // call actor with a message
    //calActor.tell("Hi there")
    calActor ! "Hi there"
    
    _system.shutdown()
  }

}