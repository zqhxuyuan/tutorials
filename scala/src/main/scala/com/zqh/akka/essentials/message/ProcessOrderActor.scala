package com.zqh.akka.essentials.message

import akka.actor.{ActorSystem, Actor, Props}
import akka.pattern.{ask, pipe}

import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * OrderHistory(Order(456,123,345.0,5),Address(456,Munish Gupta,Sarjapura Road,Bangalore, India))
 */
case class Order(userId: Int, orderNo: Int, amount: Float, noOfItems: Int)
case class Address(userId: Int, fullName: String, address1: String, address2: String)
case class OrderHistory(order: Order, address: Address)

object ProcessOrderActor{
  def main(args: Array[String]) {
    val _system = ActorSystem("FutureUsageExample")
    val processOrder = _system.actorOf(Props[ProcessOrderActor])
    processOrder ! 456
    Thread.sleep(5000)
    _system.shutdown
  }
}

class ProcessOrderActor extends Actor {

  implicit val timeout = Timeout(5 seconds)
  val orderActor = context.actorOf(Props[OrderActor])
  val addressActor = context.actorOf(Props[AddressActor])
  val orderAggregateActor = context.actorOf(Props[OrderAggregateActor])

  def receive = {
    case userId: Integer =>
      val aggResult: Future[OrderHistory] =
        for {
          order <- ask(orderActor, userId).mapTo[Order] // call pattern directly
          address <- addressActor ask userId mapTo manifest[Address] // call by implicit conversion
        } yield OrderHistory(order, address)
      aggResult pipeTo orderAggregateActor
  }
}