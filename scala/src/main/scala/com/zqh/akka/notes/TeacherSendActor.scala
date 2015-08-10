package com.zqh.akka.notes

import akka.actor.Actor
import com.zqh.akka.notes.TeacherProtocol._

import scala.util.Random

class TeacherSendActor extends Actor {

  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest => {
      //Get a random Quote from the list and construct a response
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))
      sender ! quoteResponse
    }
  }

}