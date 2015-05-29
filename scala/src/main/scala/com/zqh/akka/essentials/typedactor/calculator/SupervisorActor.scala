package com.zqh.akka.essentials.typedactor.calculator

import akka.actor.SupervisorStrategy.{Restart, _}
import akka.actor.{ActorRef, OneForOneStrategy, Props, SupervisorStrategy, TypedActor}
import akka.actor.TypedActor.{PostStop, PreStart, Supervisor}
import akka.event.Logging

import scala.concurrent.Future
import scala.concurrent.duration._

class SupervisorActor extends CalculatorInt with PreStart with PostStop with Supervisor {

  import akka.actor.TypedActor.context

  var counter: Int = 0
  val log = Logging(context.system, TypedActor.self.getClass())
  val childActor: ActorRef = context.actorOf(Props[ChildActor], name = "childActor")


  // Non blocking request response
  def add(first: Int, second: Int): Future[Int] = Future successful first + second

  // Non blocking request response
  def subtract(first: Int, second: Int): Future[Int] = Future successful first - second

  // fire and forget
  def incrementCount(): Unit = counter += 1

  // Blocking request response
  def incrementAndReturn(): Option[Int] = {
    counter += 1
    Some(counter)
  }

  def onReceive(message: Any, sender: ActorRef): Unit = {
    log.info("Message received-> {}", message)
    childActor.tell(message, sender)
  }

  // Allows to tap into the Actor PreStart hook
  def preStart(): Unit = {
    log.info("Actor Started")
  }

  // Allows to tap into the Actor PostStop hook
  def postStop(): Unit = {
    log.info("Actor Stopped")
  }

  def supervisorStrategy(): SupervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
    case _: ArithmeticException => Resume
    case _: IllegalArgumentException => Restart
    case _: NullPointerException => Stop
    case _: Exception => Escalate
  }
}

