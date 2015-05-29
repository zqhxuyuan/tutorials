package com.zqh.akka.essentials.supervisor

import akka.actor._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._
import scala.collection.immutable.HashMap
import akka.util.Timeout
import scala.concurrent.duration._

/**
 * Created by hadoop on 15-2-26.
 */
object MonitorWorker {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("faultTolerance")
    val supervisor = system.actorOf(Props[SupervisorActor], name = "supervisor")
    var mesg: Int = 8
    supervisor ! mesg
    supervisor ! "Do Something"
    Thread.sleep(4000)
    supervisor ! mesg
    system.shutdown
  }

  case class Result()
  case class DeadWorker()
  case class RegisterWorker(val worker: ActorRef, val supervisor: ActorRef)

  class SupervisorActor extends Actor with ActorLogging {
    var childActor = context.actorOf(Props[WorkerActor], name = "workerActor")
    val monitor = context.system.actorOf(Props[MonitorActor], name = "monitor")

    override def preStart() {
      monitor ! new RegisterWorker(childActor, self)
    }

    override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 10 seconds) {
      case _: ArithmeticException => Resume
      case _: NullPointerException => Restart
      case _: IllegalArgumentException => Stop
      case _: Exception => Escalate
    }

    def receive = {
      case result: Result =>
        childActor.tell(result, sender)
      case mesg: DeadWorker =>
        log.info("Got a DeadWorker message, restarting the worker")
        childActor = context.actorOf(Props[WorkerActor], name = "workerActor")
      case msg: Object =>
        childActor ! msg
    }
  }

  class WorkerActor extends Actor with ActorLogging {
    var state: Int = 0

    override def preStart() {
      log.info("Starting WorkerActor instance hashcode # {}", this.hashCode())
    }
    override def postStop() {
      log.info("Stopping WorkerActor instance hashcode # {}", this.hashCode());
    }
    def receive: Receive = {
      case value: Int =>
        state = value
      case result: Result =>
        sender ! state
      case _ =>
        context.stop(self) //worker会terminate
    }
  }

  //定义MonitorActor
  class MonitorActor extends Actor with ActorLogging {
    var monitoredActors = new HashMap[ActorRef, ActorRef]

    def receive: Receive = {
      case t: Terminated => //收到actor发来的terminated msg
        if (monitoredActors.contains(t.actor)) {
          log.info("Received Worker Actor Termination Message -> " + t.actor.path)
          log.info("Sending message to Supervisor")
          val value: Option[ActorRef] = monitoredActors.get(t.actor)
          value.get ! new DeadWorker() //通知注册的supervisor
        }
      case msg: RegisterWorker => //注册
        context.watch(msg.worker) //关键一步,调用context.watch来监控这个worker actor, 这样worker会在terminated时, 发送msg给monitor
        monitoredActors += msg.worker -> msg.supervisor //将actor和想监控他的supervisor放入map
    }
  }
}

