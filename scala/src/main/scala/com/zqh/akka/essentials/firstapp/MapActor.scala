package com.zqh.akka.essentials.firstapp

import java.util.StringTokenizer

import akka.actor.{Actor, ActorRef, actorRef2Scala}
import com.zqh.akka.essentials.firstapp.MapData

import scala.collection.immutable._

class MapActor(reduceActor: ActorRef) extends Actor {

  val STOP_WORDS_LIST = List("a", "am", "an", "and", "are", "as", "at", "be",
    "do", "go", "if", "in", "is", "it", "of", "on", "the", "to")

  val defaultCount: Int = 1

  def receive: Receive = {
    case message: String =>
      reduceActor ! evaluateExpression(message)
    case _ =>
  }

  def evaluateExpression(line: String): MapData = {
    var dataList = List[Word]()
    var parser: StringTokenizer = new StringTokenizer(line)
    while (parser.hasMoreTokens()) {
      var word: String = parser.nextToken().toLowerCase()
      if (!STOP_WORDS_LIST.contains(word)) {
        dataList = new Word(word, defaultCount) :: dataList
      }
    }
    return new MapData(dataList)
  }

  /**
  def evaluateExpression(line: String): MapData = MapData {
    line.split("""\s+""").foldLeft(ArrayBuffer.empty[WordCount]) {
      (index, word) =>
        if(!STOP_WORDS_LIST.contains(word.toLowerCase))
          index += WordCount(word.toLowerCase, 1)
        else
          index
    }
  }*/
}
