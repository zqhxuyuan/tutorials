package com.zqh.akka.essentials.grid

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import akka.actor.ActorSystem
import akka.actor.Address
import akka.actor.PoisonPill
import akka.actor.Props
import akka.dispatch.PriorityGenerator
import akka.dispatch.UnboundedPriorityMailbox
import akka.remote.{RemotingLifecycleEvent}

case class Task(taskNumber: Int)
case class StartWorker(actorPath: String)
case class StopWorker(actorPath: String)
case class TaskFinished(taskNumber: Int)
case class ENOUGH()
case class SendWork()
case class STOP()

object WorkServerActorSystem {

  def main(args: Array[String]): Unit = {}

  // load the configuration
  val system = ActorSystem.create("WorkServerSys", ConfigFactory.load().getConfig("WorkServerSys"));
  val log = system.log

  // create the work scheduler actor
  val workSchedulerActor = system.actorOf(Props[WorkSchedulerActor], name = "WorkSchedulerActor");

  // create the job controller actor, which manages the routees and sends out work packets to the registered workers
  val jobControllerActor = system.actorOf(Props(new JobControllerActor(workSchedulerActor)), name = "JobControllerActor");

  // actor that registers and unregisters the workers
  val registerRemoteWorkerActor = system.actorOf(Props(new RegisterRemoteWorkerActor(jobControllerActor)), name = "RegisterRemoteWorkerActor");

  //val remoteActorListener = system.actorOf(Props(new RemoteClientEventListener(jobControllerActor)), name = "RemoteClientEventListener");
  //system.eventStream.subscribe(remoteActorListener, classOf[RemoteLifeCycleEvent])
  //system.eventStream.subscribe(remoteActorListener, classOf[RemotingLifecycleEvent])

  /**
   * Create a unbounded priority mailbox to make sure that the display_list
   * message has the least priority. The standard text messages get processed earlier than that.
   */
  class MyUnboundedPriorityMailbox(settings: ActorSystem.Settings, config: Config) extends UnboundedPriorityMailbox(
    // Create a new PriorityGenerator
    PriorityGenerator {
      // 'highpriority messages should be treated first if possible
      case Address => 0
      // Worker says no more work for me
      case ENOUGH => 2
      // acknowledge the message processed
      case TaskFinished => 1
      // PoisonPill when no other left
      case PoisonPill => 4
      // We default to 3
      case otherwise => 5
    })
}