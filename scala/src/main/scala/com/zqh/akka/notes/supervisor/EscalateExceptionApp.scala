package com.zqh.akka.notes.supervisor

/**
 * Created by zqhxuyuan on 15-8-11.
 */
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy.Escalate
import akka.actor.SupervisorStrategy.Stop
import akka.actor.actorRef2Scala

object EscalateExceptionApp extends App {

  val actorSystem = ActorSystem("EscalateExceptionSystem")
  val actor = actorSystem.actorOf(Props[EscalateExceptionTopLevelActor], "topLevelActor")
  actor ! "create_parent"
}

class EscalateExceptionTopLevelActor extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception => {
      log.info("The exception from the Child is now handled by the Top level Actor. Stopping Parent Actor and its children.")
      Stop //Stop will stop the Actor that threw this Exception and all its children
    }
  }

  def receive = {
    case "create_parent" => {
      log.info("creating parent")
      val parent = context.actorOf(Props[EscalateExceptionParentActor], "parentActor")
      parent ! "create_child" //Sending message to next level
    }
  }
}

class EscalateExceptionParentActor extends Actor with ActorLogging {

  override def preStart={
    log.info ("Parent Actor started")
  }

  override val supervisorStrategy = OneForOneStrategy() {
    case _: Exception => {
      log.info("The exception is ducked by the Parent Actor. Escalating to TopLevel Actor")
      Escalate
    }
  }

  def receive = {
    case "create_child" => {
      log.info("creating child")
      val child = context.actorOf(Props[EscalateExceptionChildActor], "childActor")
      child ! "throwSomeException"
    }
  }

  override def postStop = {
    log.info("Stopping parent Actor")
  }
}

class EscalateExceptionChildActor extends akka.actor.Actor with ActorLogging {

  override def preStart={
    log.info ("Child Actor started")
  }

  def receive = {
    case "throwSomeException" => {
      throw new Exception("I'm getting thrown for no reason.")
    }
  }
  override def postStop = {
    log.info("Stopping child Actor")
  }
}