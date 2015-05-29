package com.zqh.akka.essentials.supervisor

import akka.actor._
import akka.util.Timeout
import akka.pattern.ask
import akka.actor.SupervisorStrategy._
import scala.concurrent.Await
import scala.concurrent.duration._


/**
 * Created by hadoop on 15-2-27.
 */
object TwoWorkers {

  def main(args: Array[String]) {
    val system = ActorSystem("faultTolerance")
    val log = system.log
    val originalValue: Int = 0

    val supervisor = system.actorOf(Props[SupervisorActor], name = "supervisor")

    log.info("Sending value 8, no com.github.kowshik.bigo.exceptions should be thrown! ")
    var mesg: Int = 8
    supervisor ! mesg

    implicit val timeout = Timeout(5 seconds)
    var future = (supervisor ? new Result).mapTo[Int]
    var result = Await.result(future, timeout.duration)

    log.info("Value Received-> {}", result)

    log.info("Sending value -8, ArithmeticException should be thrown! Our Supervisor strategy says resume !")
    mesg = -8
    supervisor ! mesg

    future = (supervisor ? new Result).mapTo[Int]
    result = Await.result(future, timeout.duration)

    log.info("Value Received-> {}", result)

    log.info("Sending value null, NullPointerException should be thrown! Our Supervisor strategy says restart !")
    supervisor ! new NullPointerException

    future = (supervisor ? new Result).mapTo[Int]
    result = Await.result(future, timeout.duration)

    log.info("Value Received-> {}", result)

    log.info("Sending value \"String\", IllegalArgumentException should be thrown! Our Supervisor strategy says Stop !")

    supervisor ? "Do Something"

    log.info("Worker Actors shutdown !")

    system.shutdown
  }

  case class Result()

  class SupervisorActor extends Actor with ActorLogging {
    val workerActor1 = context.actorOf(Props[WorkerActor1], name = "workerActor1")
    //val workerActor2 = context.actorOf(Props[WorkerActor2], name = "workerActor2")

    override val supervisorStrategy = AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

    def receive = {
      case result: Result =>
        workerActor1.tell(result, sender)
      case msg: Object =>
        workerActor1 ! msg

    }
  }

  class WorkerActor1 extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("Starting WorkerActor instance hashcode # {}", this.hashCode())
    }
    override def postStop() {
      log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode())
    }
    def receive: Receive = {
      case value: Int =>
        if (value <= 0)
          throw new ArithmeticException("Number equal or less than zero")
        else
          state = value
      case result: Result =>
        sender ! state
      case ex: NullPointerException =>
        throw new NullPointerException("Null Value Passed")
      case _ =>
        throw new IllegalArgumentException("Wrong Argument")
    }
  }

  class WorkerActor2 extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("Starting WorkerActor instance hashcode # {}", this.hashCode())
    }
    override def postStop() {
      log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode())
    }
    def receive: Receive = {
      case value: Int =>
        if (value <= 0)
          throw new ArithmeticException("Number equal or less than zero")
        else
          state = value
      case result: Result =>
        sender ! state
      case ex: NullPointerException =>
        throw new NullPointerException("Null Value Passed")
      case _ =>
        throw new IllegalArgumentException("Wrong Arguement")
    }
  }
}
