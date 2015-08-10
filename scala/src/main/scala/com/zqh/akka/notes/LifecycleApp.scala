package com.zqh.akka.notes

import akka.actor._
import akka.event.LoggingReceive

class BasicLifecycleLoggingActor extends Actor with ActorLogging{

  log.info ("Inside BasicLifecycleLoggingActor Constructor")
  log.info (context.self.toString())

  override def preStart() ={
    log.info("Inside the preStart method of BasicLifecycleLoggingActor")
  }

  def receive = LoggingReceive{
    case "hello" => log.info ("hello")
    case "stop" => context.stop(self)
  }

  override def postStop()={
    log.info ("Inside postStop method of BasicLifecycleLoggingActor")
  }

}

object LifecycleApp extends App{

  val actorSystem = ActorSystem("LifecycleActorSystem")
  val lifecycleActor = actorSystem.actorOf(Props[BasicLifecycleLoggingActor],"lifecycleActor")

  //DeadLetter Actor会处理自己邮箱里的消息，并把每条消息都封装成一个DeadLetter，然后再发布到EventStream里面去。
  //DeadLetterListener的Actor会去消费所有这些DeadLetter消息并把它们作为一条日志消息发布出去
  val deadLetterListener = actorSystem.actorOf(Props[MyCustomDeadLetterListener])
  //MyCustomDeadLetterListener监听器订阅了DeadLetter的EventStream,监听器必须也是一个Actor.并且对接收到的DeadLetter消息进行处理
  actorSystem.eventStream.subscribe(deadLetterListener, classOf[DeadLetter])

  lifecycleActor ! "hello"
  lifecycleActor!"stop"
  //Actor被stop后,发往这个Actor的消息都是DeadLetter
  lifecycleActor!"hello" //Sending message to an Actor which is already stopped: dead letter


  //wait for a couple of seconds before shutdown
  Thread.sleep(2000)
  actorSystem.shutdown()
}

//订阅者只需要是一个Actor就可以订阅EventStream的topic: 这里的topic是DeadLetter事件/消息
//正如前面的日志也是topic,生产日志,或者生产出dead letter,都是一个消息,会被订阅者/监听器消费
class MyCustomDeadLetterListener extends Actor {
  def receive = {
    case deadLetter: DeadLetter => println(s"FROM CUSTOM LISTENER $deadLetter")
  }
}