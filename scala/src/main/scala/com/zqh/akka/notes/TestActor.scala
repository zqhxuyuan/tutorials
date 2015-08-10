package com.zqh.akka.notes

import akka.actor.{Props, ActorSystem}
import akka.testkit._
import com.typesafe.config.ConfigFactory
import com.zqh.akka.notes.TeacherProtocol._
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import scala.concurrent.duration._

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

    //,ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")
    ,ConfigFactory.parseString("""
                                akka{
                                  loggers = ["akka.testkit.TestEventListener"]
                                  test{
                                      filter-leeway = 7s
                                  }
                                }
                              """)
  ))
  with WordSpecLike with MustMatchers with BeforeAndAfterAll with ImplicitSender {

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

  //Requet and Response
  "A student" must {
    "log a QuoteResponse eventually when an InitSignal is sent to it" in {
      val teacherRef = system.actorOf(Props[TeacherSendActor], "teacherActor")
      val studentRef = system.actorOf(Props(new StudentActor(teacherRef)), "studentActor")

      EventFilter.info (start="Printing from Student Actor", occurrences=1).intercept{
        studentRef!InitSignal
      }
    }
  }

  val config=ConfigFactory.parseString("""akka.loggers = ["akka.testkit.TestEventListener"]""")
  val systemTest=ActorSystem("UniversityMessageSystem", config.withFallback(ConfigFactory.load()))
  println (system.settings.config.getValue("akka.loggers")) //> SimpleConfigList(["akka.testkit.TestEventListener"])

  //测试调度: Student在收到InitSignal后过5秒后才发送消息给Teacher
  //确保是TearcherSendActor, 如果是TeacherActor, 没有向StudentActor发送响应, 则会报错:
  //Timeout (7000 milliseconds) waiting for 1 messages on InfoFilter(None,Left(Printing from Student Actor),false)
  "A delayed student" must {
    "fire the QuoteRequest after 5 seconds when an InitSignal is sent to it" in {
      val teacherRef = system.actorOf(Props[TeacherSendActor], "teacherActorDelayed")
      val studentRef = system.actorOf(Props(new StudentDelayActor(teacherRef)), "studentDelayedActor")

      EventFilter.info (start="Printing from Student Actor", occurrences=1).intercept{
        studentRef!InitSignal
      }
    }
  }

  //DeathWatch
  "A QuoteRepositoryActor" must {
    "send back a termination message to the watcher on 4th message" in {
      val quoteRepository = TestActorRef[QuoteRepositoryActor]

      //用一个测试Actor:TestProbe,监视RepositoryActor
      val testProbe = TestProbe()
      testProbe.watch(quoteRepository) //Let's watch the Actor

      within(1000 millis) {
        var receivedQuotes = List[String]()
        //向RepositoryActor发送三次消息
        (1 to 3).foreach(_ => quoteRepository ! QuoteRequest)
        //QuoteRepositoryActor中如果未满三次,回向sender发送Response
        receiveWhile() {
          //每次收到QuoteRepositoryActor的回应,将消息加入到列表中
          case QuoteResponse(quoteString) => {
            receivedQuotes = receivedQuotes :+ quoteString
          }
        }
        receivedQuotes.size must be(3)
        println(s"receiveCount ${receivedQuotes.size}")

        //4th message 当向QuoteRepositoryActor发送第四条消息时,
        //QuoteRepositoryActor的recive方法会判断到count>3,导致它把自己杀死了!
        //因为TestProbe一直监控着QuoteRepositoryActor,这时候QuoteRepositoryActor已经被自己杀死了
        //监控者TestProbe就能收到QuoteRepositoryActor被终结的信息.
        quoteRepository ! QuoteRequest
        testProbe.expectTerminated(quoteRepository) //Expect a Terminated Message
      }
    }

    "not send back a termination message on 4th message if not watched" in {
      val quoteRepository=TestActorRef[QuoteRepositoryActor]
      val testProbe=TestProbe()
      testProbe.watch(quoteRepository) //watching

      within (1000 millis) {
        var receivedQuotes = List[String]()
        (1 to 3).foreach(_ => quoteRepository ! QuoteRequest)
        receiveWhile() {
          case QuoteResponse(quoteString) => {
            receivedQuotes = receivedQuotes :+ quoteString
          }
        }

        testProbe.unwatch(quoteRepository) //not watching anymore
        receivedQuotes.size must be (3)
        println(s"receiveCount ${receivedQuotes.size}")

        //4th message
        quoteRepository!QuoteRequest
        testProbe.expectNoMsg() //Not Watching. No Terminated Message
      }
    }

    "end back a termination message to the watcher on 4th message to the TeacherActor" in {
      //This just subscribes to the EventFilter for messages.
      //We have asserted all that we need against the QuoteRepositoryActor in the previous testcase
      val teacherActor=TestActorRef[TeacherActorWatcher]

      within (1000 millis) {
        (1 to 3).foreach (_ => teacherActor!QuoteRequest) //this sends a message to the QuoteRepositoryActor

        EventFilter.error (pattern="""Child Actor .* Terminated""", occurrences = 1).intercept{
          teacherActor!QuoteRequest //Send the dangerous 4th message
        }
      }
    }
  }


}