package com.zqh.akka.notes.supervisor

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{AllForOneStrategy, Props, ActorLogging, Actor}
import com.zqh.akka.notes.QuoteRepositoryActor
import com.zqh.akka.notes.TeacherProtocol.QuoteRequest

/**
 * Created by zqhxuyuan on 15-8-11.
 *
 * 输出: 说明使用AllForOne, 只要一个子Actor Dead, 其他子Actor都会被杀死
 * 怎么验证子Actor有没有被杀死, 重载postStop方法,如果打印,说明这个子Actor被杀死
 * OK.
  ChildActor 2 Dead...
  ChildActor 1 Dead...
 */
class TeacherActorAllForOne extends Actor with ActorLogging {

  var requestCount: Int = 0

  val quoteRepository = context.actorOf(Props[QuoteRepositoryActor])
  val child1 = context.actorOf(Props[ChildActor])
  val child2 = context.actorOf(Props[ChildActor2])

  override val supervisorStrategy = AllForOneStrategy() {
    case _: RepoDownException => Stop
    case _: Exception => Escalate
  }

  def receive = {
    case QuoteRequest => {
      //if (requestCount > 3) throw new RepoDownException("Simulating a dummy exception after 3 requests")
      child1 ! QuoteRequest
      child2 ! QuoteRequest
    }
    case _ => println("...")
  }
}

class ChildActor extends Actor with ActorLogging {
  override def postStop()={
    log.info ("ChildActor 1 Dead...")
    println("ChildActor 1 Dead...")
  }
  def receive = {
    case QuoteRequest => println("OK.")
    case _ => println("...")
  }
}

class ChildActor2 extends Actor with ActorLogging {
  override def postStop()={
    log.info ("ChildActor 2 Dead...")
    println("ChildActor 2 Dead...")
  }
  def receive = {
    case QuoteRequest => throw new RepoDownException("ERROR!")
    case _ => println("...")
  }
}