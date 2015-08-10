package com.zqh.akka.notes

import akka.actor._
import com.zqh.akka.notes.TeacherProtocol._
import scala.util.Random

object DeathWatcherApp extends App{
  val system = ActorSystem("UniversityMessageSystem")
  val teacherRef = system.actorOf(Props[TeacherActorWatcher], "teacherActorWatcher")
  teacherRef ! QuoteRequest
  Thread.sleep(5000)
  system.shutdown
}

//被观察者
class QuoteRepositoryActor() extends Actor with ActorLogging {
  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")
  var repoRequestCount:Int=1

  def receive = {
    case QuoteRequest => {
      if (repoRequestCount>3){
        self!PoisonPill
      } else {
        //Get a random Quote from the list and construct a response
        val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))

        log.info(s"QuoteRequest received in QuoteRepositoryActor. Sending response to Teacher Actor $quoteResponse")
        repoRequestCount=repoRequestCount+1
        sender ! quoteResponse
      }
    }
  }
}

//观察者, Teacher观察Repository,一旦Repository挂掉,Teacher就能收到Repository被终结的信息
//Teacher观察学生考试,一旦学生发生作弊行为, 老师就能收到这个信息. 其实类似于监听器.
class TeacherActorWatcher extends Actor with ActorLogging {
  val quoteRepositoryActor=context.actorOf(Props[QuoteRepositoryActor], "quoteRepositoryActor")
  context.watch(quoteRepositoryActor)

  def receive = {
    case QuoteRequest =>
      quoteRepositoryActor ! QuoteRequest
    //case QuoteResponse =>
    //  quoteRepositoryActor ! QuoteRequest
    case Terminated(terminatedActorRef)=>
      log.error(s"Child Actor {$terminatedActorRef} Terminated")
  }
}