package com.zqh.akka.notes

import com.zqh.akka.notes.TeacherProtocol._

import scala.util.Random
import util.Random

import akka.actor.Actor

/**
 * Created by zqhxuyuan on 15-8-8.
 *
 * Your Teacher Actor class.
 *
 * The class could use refinement by way of
 * using ActorLogging which uses the EventBus of the Actor framework
 * instead of the plain old System out
 */
class TeacherActor extends Actor {

  val quotes = List(
    "Moderation is for cowards",
    "Anything worth doing is worth overdoing",
    "The trouble is you think you have time",
    "You never gonna know if you never even try")

  def receive = {
    case QuoteRequest => {
      //Get a random Quote from the list and construct a response
      val quoteResponse = QuoteResponse(quotes(Random.nextInt(quotes.size)))
      println (quoteResponse)
    }
  }

}