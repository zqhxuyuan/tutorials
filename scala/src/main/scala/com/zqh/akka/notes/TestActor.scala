package com.zqh.akka.notes

import akka.actor.{Props, ActorSystem}
import akka.testkit.{EventFilter, TestActorRef, TestKit}
import com.typesafe.config.ConfigFactory
import com.zqh.akka.notes.TeacherProtocol._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class TestActor extends TestKit(
  ActorSystem("UniversityMessageSystem"
    //all log messages go to the EventStream and the SLF4JLogger subscribes to it and uses its appenders to write to the log file/console etc
    //默认Actor的log.info将消息发布到EventStream中. 会由消息的订阅者即application.conf中的loggers:SLF4JLogger
    //通过logback.xml配置的日志输出到File中(或者可以配置到控制台)
    //为了测试,我们重载loggers, 以便能够在测试方法中直接看到日志
    //EventStream is like a topic. log.info just like publish msg to the topic
    //And our config Logger subscribe the topic, once producer publish msg to topic, the consumer will receive the msg from the topic,
    //then the consumer:Logger can do it on his own, like write to file or output to std console
    //And If we overwrite the subscriber, like Logger before, the TestEventListener will do the same job in his way.
    ,ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")
  ))
  with WordSpecLike with MustMatchers with BeforeAndAfterAll {

  //1. Sends message to the Print Actor. Not even a testcase actually
  "A teacher" must {
    "print a quote when a QuoteRequest message is sent" in {
      val teacherRef = TestActorRef[TeacherActor]
      teacherRef ! QuoteRequest
    }
  }

  //2. Sends message to the Log Actor. Again, not a testcase per se
  "A teacher with ActorLogging" must {
    "log a quote when a QuoteRequest message is sent" in {
      val teacherRef = TestActorRef[TeacherLogActor]
      teacherRef ! QuoteRequest
    }
  }

  //3. Asserts the internal State of the Log Actor.
  "have a quote list of size 4" in {
    val teacherRef = TestActorRef[TeacherLogActor]
    teacherRef.underlyingActor.quoteList must have size (4)
    teacherRef.underlyingActor.quoteList must have size (4)
  }

  //4. Verifying log messages from eventStream
  "be verifiable via EventFilter in response to a QuoteRequest that is sent" in {
    val teacherRef = TestActorRef[TeacherLogActor]
    EventFilter.info(pattern = "QuoteResponse*", occurrences = 1) intercept {
      teacherRef ! QuoteRequest
    }
  }

  //5. have a quote list of the same size as the input parameter
  " have a quote list of the same size as the input parameter" in {
    val quotes = List(
      "Moderation is for cowards",
      "Anything worth doing is worth overdoing",
      "The trouble is you think you have time",
      "You never gonna know if you never even try")

    val teacherRef = TestActorRef(new TeacherLogParameterActor(quotes))
    //val teacherRef = TestActorRef(Props(new TeacherLogParameterActor(quotes)))

    teacherRef.underlyingActor.quoteList must have size (4)
    EventFilter.info(pattern = "QuoteResponse*", occurrences = 1) intercept {
      teacherRef ! QuoteRequest
    }
  }

  "A student" must {
    "log a QuoteResponse eventually when an InitSignal is sent to it" in {
      val teacherRef = system.actorOf(Props[TeacherSendActor], "teacherActor")
      val studentRef = system.actorOf(Props(new StudentActor(teacherRef)), "studentActor")

      EventFilter.info (start="Printing from Student Actor", occurrences=1).intercept{
        studentRef!InitSignal
      }
    }
  }
}