package com.zqh.akka.essentials.stm.pingpong

import scala.concurrent.ExecutionContext.Implicits.global
import akka.agent.Agent

/**
 * Created by hadoop on 15-2-27.
 */
object AgentTest extends App{

  val agent = Agent(5)

  // send a value, enqueues this change of the value of the Agent
  agent send 7

  // send a function, enqueues this change to the value of the Agent
  agent send (_ + 1)
  agent send (_ * 2)

  val result = agent()
  println(result)
}
