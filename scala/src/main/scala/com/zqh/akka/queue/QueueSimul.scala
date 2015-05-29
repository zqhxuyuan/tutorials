package com.zqh.akka.queue

import akka.actor.{ActorLogging, Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
 * http://blog.artek-consulting.com/the-art-of-queueing-simulated/
 */
object QueueSimul extends App {
  val system = ActorSystem("queue-simul")
  val startTime = System.currentTimeMillis

  // Read configuration
  val conf = ConfigFactory.load
  lazy val timespan = (conf.getString("queue-sample.tubestation-lifespan")).toLong * 1000

  // Create the Tube Station and open it
  val station = system.actorOf(Props(new TubeStation), "tubeStation")
  station ! Start

  Thread.sleep(timespan)
  station ! Stop

  //system.shutdown()
  system.awaitTermination()
  //log.info(s"Simulation terminating. Has run ${System.currentTimeMillis - startTime} s")
}