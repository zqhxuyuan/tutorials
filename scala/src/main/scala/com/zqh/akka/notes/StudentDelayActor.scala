package com.zqh.akka.notes

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.zqh.akka.notes.TeacherProtocol._

import scala.concurrent.duration._

/**
 * Created by zqhxuyuan on 15-8-8.
 */
class StudentDelayActor (teacherActorRef:ActorRef) extends Actor with ActorLogging {

  def receive = {
    case InitSignal=> {
      //直接发送,没有任何延迟
      //teacherActorRef!QuoteRequest

      //一次性调度,开始时间延迟5秒
      import context.dispatcher
      context.system.scheduler.scheduleOnce(5 seconds, teacherActorRef, QuoteRequest)

      //定时调度: 没有延迟,每隔5秒发送一次
      //context.system.scheduler.schedule(0 seconds, 5 seconds, teacherActorRef, QuoteRequest)
    }

    case QuoteResponse(quoteString) => {
      log.info ("Received QuoteResponse from Teacher")
      log.info(s"Printing from Student Actor $quoteString")
    }
  }
}