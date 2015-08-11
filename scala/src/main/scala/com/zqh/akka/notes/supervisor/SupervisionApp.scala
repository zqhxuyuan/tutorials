package com.zqh.akka.notes.supervisor

import akka.actor.{Props, ActorSystem}
import com.zqh.akka.notes.TeacherProtocol.QuoteRequest

/**
 * Created by zqhxuyuan on 15-8-11.
 */
object SupervisionApp extends App{

  val actorSystem = ActorSystem("SupervisionActorSystem")

  /*
  val teacherProps = Props[TeacherActorAllForOne]
  val teacherSupervisor = actorSystem.actorOf(Props(new DepartmentSupervisor(teacherProps)),"teacherSupervisor")
  teacherSupervisor ! QuoteRequest
  */

  val teacherSupervisor1 = actorSystem.actorOf(Props[TeacherActorOneForOne],"teacherSupervisor")
  teacherSupervisor1 ! QuoteRequest

  /*
  val teacherSupervisor2 = actorSystem.actorOf(Props[TeacherActorAllForOne],"teacherSupervisor")
  teacherSupervisor2 ! QuoteRequest
  */

  Thread.sleep(5000)
  actorSystem.shutdown()
}
