package com.zqh.akka.notes

/**
 * Created by zqhxuyuan on 15-8-8.
 */
object TeacherProtocol{

  case class QuoteRequest()
  case class QuoteResponse(quoteString:String)
  case class InitSignal()

}