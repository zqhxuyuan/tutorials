package com.zqh.akka.notes.supervisor

import akka.actor.SupervisorStrategy.{Stop, Restart}
import akka.actor._

/**
 * Created by zqhxuyuan on 15-8-11.
 */
object DefaultStrategyApp extends App{

  val actorSystem=ActorSystem("ActorInitializationException")

  //默认的异常处理机制
  val supervisorStrategy=OneForOneStrategy() {
    case _: ActorInitializationException=> Stop
    case _: ActorKilledException        => Stop
    case _: DeathPactException          => Stop
    case _: Exception                   => Restart
  }

  /*
  val actor=actorSystem.actorOf(Props[ActorInitializationExceptionActor], "initializationExceptionActor")
  actor!"someMessageThatWillGoToDeadLetter"


  val actor=actorSystem.actorOf(Props[ActorKilledExceptionActor])
  actor!"something"
  actor!Kill
  actor!"something else that falls into dead letter queue"

  val actor=actorSystem.actorOf(Props[DeathPactExceptionParentActor])
  actor!"create_child" //Throws DeathPactException
  Thread.sleep(2000) //Wait until Stopped
  actor!"someMessage" //Message goes to DeadLetters
  */

  val actor=actorSystem.actorOf(Props[OtherExceptionParentActor])
  actor!"create_child"


  Thread.sleep(5000)
  actorSystem.shutdown
}

class ActorInitializationExceptionActor extends Actor with ActorLogging{
  override def preStart={
    throw new Exception("Some random exception")
  }
  def receive={
    case _=>
  }
}

class ActorKilledExceptionActor extends Actor with ActorLogging{
  def receive={
    case message:String=> log.info (message)
  }
}

class DeathPactExceptionParentActor extends Actor with ActorLogging{
  override def postStop()={
    println("Parent Dead...")
  }
  def receive={
    case "create_child"=> {
      log.info ("creating child")
      val child=context.actorOf(Props[DeathPactExceptionChildActor])
      context.watch(child) //Watches but doesnt handle terminated message. Throwing DeathPactException here.
      child!"stop"
    }
    case "someMessage" => log.info ("some message")
    //Doesnt handle terminated message
    //如果Parent watch了Child Actor, 则它应当处理Child Actor的Terminated消息,
    //如果没有处理这个消息,则会抛出DeathPactException异常! 然后自己也会被Stop了!
    //既然你向监视别人,那么你就必须要处理对方被杀死的消息.
    //case Terminated(_) =>
  }
}
class DeathPactExceptionChildActor extends Actor with ActorLogging{
  override def postStop()={
    println("ChildActor Dead...")
  }
  def receive={
    case "stop"=> {
      log.info ("Actor going to stop and announce that it's terminated")
      self!PoisonPill
    }
  }
}

class OtherExceptionParentActor extends Actor with ActorLogging{
  def receive={
    case "create_child"=> {
      log.info ("creating child")
      val child=context.actorOf(Props[OtherExceptionChildActor])

      child!"throwSomeException"
      child!"someMessage"
    }
  }
}

class OtherExceptionChildActor extends akka.actor.Actor with ActorLogging{
  override def preStart={
    log.info ("Starting Child Actor")
  }
  def receive={
    case "throwSomeException"=> {
      throw new Exception ("I'm getting thrown for no reason")
    }
    case "someMessage" => log.info ("Restarted and printing some Message")
  }
  override def postStop={
    log.info ("Stopping Child Actor")
  }
}

