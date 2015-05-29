package com.zqh.akka.essentials.dispatcher

import java.util.Random
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.routing._
import akka.util.Timeout
import scala.concurrent.duration._
import akka.pattern.ask

import scala.concurrent.Await

/**
 * Created by hadoop on 15-2-26.
 */
object RouterExample extends App{

  val _system = ActorSystem("BroadcastRouterExample")
  run("SGFCRouter")

  _system.shutdown

  def run(router : String) {
    router match {
      case "BroadcastRouter" =>
        val actor = _system.actorOf(Props[MsgEchoActor].withRouter(BroadcastRouter(5)), name = "BroadcastRouter")
        1 to 10 foreach {
          i => actor ! i
        }

      case "RandomRouter" =>
        val actor = _system.actorOf(Props[MsgEchoActor].withRouter(RandomRouter(5)), name = "RandomRouter")
        1 to 10 foreach {
          i => actor ! i
        }

      case "RoundRobinRouter" =>
        val actor = _system.actorOf(Props[MsgEchoActor].withRouter(RoundRobinRouter(5)), name = "RoundRobinRouter")
        1 to 10 foreach {
          i =>
            actor ! i
            if (i == 5) {
              TimeUnit.MILLISECONDS.sleep(100)
              System.out.println("\n")
            }
        }

      case "SmallestMailboxRouter" =>
        val actor = _system.actorOf(Props[MsgEchoActor].withRouter(SmallestMailboxRouter(5)), name = "SmallestMailboxRouter")
        1 to 10 foreach {
          i => actor ! i
        }

      case "SGFCRouter" =>
        class RandomTimeActor extends Actor with ActorLogging{
          val randomGenerator = new Random()
          def receive: Receive = {
            case message =>
              val sleepTime = randomGenerator.nextInt(5)
              log.info("Actor # {} will return in {}", self.path.name, sleepTime)
              TimeUnit.SECONDS.sleep(sleepTime)
              sender ! "Message from Actor #" + self.path
          }
        }

        val scatterGatherFirstCompletedRouter = _system.actorOf(Props[RandomTimeActor].withRouter(
          ScatterGatherFirstCompletedRouter(nrOfInstances = 5, within = 5 seconds)), name = "mySGFCRouterActor")

        implicit val timeout = Timeout(5 seconds)
        val futureResult = scatterGatherFirstCompletedRouter ? "message"
        val result = Await.result(futureResult, timeout.duration)
        System.out.println(result)

      case _ => println("NON COMPATIBLE ROUTER")
    }
  }
}
