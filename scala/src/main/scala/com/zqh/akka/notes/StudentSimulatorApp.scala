package com.zqh.akka.notes

import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import com.zqh.akka.notes.TeacherProtocol._

object StudentSimulatorApp extends App{

  //Initialize the ActorSystem
  val actorSystem = ActorSystem("UniversityMessageSystem")

  //construct the Teacher Actor Ref
  //val teacherActorRef = actorSystem.actorOf(Props[TeacherActor])
  val teacherActorRef = actorSystem.actorOf(Props[TeacherLogActor])

  //send a message to the Teacher Actor
  teacherActorRef ! QuoteRequest

  //Let's wait for a couple of seconds before we shut down the system
  Thread.sleep (2000)

  //Shut down the ActorSystem.
  actorSystem.shutdown()
}
