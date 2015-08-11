package com.zqh.akka.notes.supervisor

import akka.actor.Actor
import akka.actor.ActorKilledException
import akka.actor.ActorLogging
import akka.actor.DeathPactException
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy.Restart
import akka.actor.SupervisorStrategy.Stop
import com.zqh.akka.notes.TeacherProtocol.QuoteRequest

/**
 * Created by zqhxuyuan on 15-8-11.
 *
 * OK.
ChildActor 2 Dead...
 */
class TeacherActorOneForOne extends Actor with ActorLogging {

  val quoteRepositoryActor = context.actorOf(Props[QuoteRepositoryExceptionThrowingActor], "quoteRepositoryActor")
  val child1 = context.actorOf(Props[ChildActor])
  val child2 = context.actorOf(Props[ChildActor2])

  override val supervisorStrategy = OneForOneStrategy() {
    case _: RepoDownException	=> Restart
    case _: Exception         => Stop
  }

  var requestCount = 0

  def receive = {
    /*
    case QuoteRequest => {
      if (requestCount <= 3) quoteRepositoryActor ! QuoteRequest
      else throw new RepoDownException("Simulating a dummy exception after 3 requests") //Let's simulate an error
    }
    */
    case QuoteRequest => {
      child1 ! QuoteRequest
      child2 ! QuoteRequest
    }
  }
}

