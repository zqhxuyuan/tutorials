package com.zqh.akka.notes.supervisor

import akka.actor.{ActorLogging, Actor, Props}
import com.zqh.akka.notes.TeacherProtocol.QuoteRequest

/**
 * Created by zqhxuyuan on 15-8-11.
 */
class DepartmentSupervisor (teacherProps:Props) extends Actor with ActorLogging {

  val teacherActor=context.actorOf(teacherProps, "teacherActor")
  log.info ("Teacher Actor path : "+ teacherActor.path)


  def receive = {
    //case qr@QuoteRequest=> teacherActor.forward(qr)
    case qr : QuoteRequest => teacherActor ! qr
  }
}
