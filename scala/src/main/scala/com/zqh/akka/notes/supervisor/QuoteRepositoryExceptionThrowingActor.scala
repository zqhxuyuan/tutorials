package com.zqh.akka.notes.supervisor

import akka.actor.{ActorLogging, Actor}
import com.zqh.akka.notes.TeacherProtocol._

import scala.util.Random

/**
 * Created by zqhxuyuan on 15-8-11.
 */
class QuoteRepositoryExceptionThrowingActor() extends Actor with ActorLogging {

  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  var repoRequestCount=0

  def receive = {

    case QuoteRequest => {

      if (repoRequestCount<3){
        throw new RepoDownException("I am going down down down")
      }
      else {
        //Get a random Quote from the list and construct a response
        val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))

        log.info("QuoteRequest received in QuoteRepositoryActor. Sending response to Sender")
        sender ! quoteResponse
      }
    }
  }
}