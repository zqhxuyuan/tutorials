package com.zqh.akka.essentials.dispatcher

import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinRouter
import com.typesafe.config.ConfigFactory

/**
 * Created by hadoop on 15-2-26.
 */
object DispatcherConfExample extends App{

  val _system = ActorSystem.create("dispatcher-demos",ConfigFactory.load().getConfig("MyDispatcherExample"))

  //test("defaultDispatcher")
  //test("defaultDispatcher1")
  test("pinnedDispatcher")
  //test("CallingThreadDispatcher")
  //test("balancingDispatcher")
  //test("balancingDispatcher1")


  def test(name : String){
    val actor = _system.actorOf(Props[MsgEchoActor].withDispatcher(name).withRouter(
      RoundRobinRouter(5)))

    0 to 25 foreach {
      i => actor ! i
    }
    Thread.sleep(3000)
    _system.shutdown()
  }

}

