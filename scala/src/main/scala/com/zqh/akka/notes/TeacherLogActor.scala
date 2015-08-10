package com.zqh.akka.notes

import akka.actor.{ActorLogging, Actor}
import com.zqh.akka.notes.TeacherProtocol._
import util.Random

/**
 * Created by zqhxuyuan on 15-8-8.
 */
class TeacherLogActor extends Actor with ActorLogging {

  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest => {
      //get a random element (for now)
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))
      log.info(quoteResponse.toString())
    }
  }

  //We'll cover the purpose of this method in the Testing section
  def quoteList = quotes

}
