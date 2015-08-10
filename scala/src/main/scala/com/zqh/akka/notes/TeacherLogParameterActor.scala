package com.zqh.akka.notes

import akka.actor.{Actor, ActorLogging}
import com.zqh.akka.notes.TeacherProtocol._

import scala.util.Random

/**
  * Created by zqhxuyuan on 15-8-8.
  */
class TeacherLogParameterActor(quotes : List[String]) extends Actor with ActorLogging {

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
