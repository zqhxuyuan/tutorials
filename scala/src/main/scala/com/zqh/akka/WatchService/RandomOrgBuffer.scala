package com.zqh.akka.WatchService

import java.util.Random

import akka.actor._
import akka.event.LoggingReceive
import akka.pattern.ask
import akka.util.Timeout

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration._

case object RandomRequest

class RandomOrgBuffer extends Actor with ActorLogging {

	val BatchSize = 50

	val buffer = new mutable.Queue[Int]
	val backlog = new mutable.Queue[ActorRef]

	val randomOrgClient = context.actorOf(Props[RandomOrgClient], name="client")

	override def preStart() {
		preFetchIfAlmostEmpty()
	}

	def receive = LoggingReceive {
		case RandomRequest =>
			preFetchIfAlmostEmpty()
			handleOrQueueInBacklog()
	}

	def handleOrQueueInBacklog() {
		if (buffer.isEmpty) {
			backlog += sender
		} else {
			sender ! buffer.dequeue()
		}
	}

	def receiveWhenWaiting = LoggingReceive {
		case RandomRequest =>
			handleOrQueueInBacklog()
		case RandomOrgServerResponse(randomNumbers) =>
			buffer ++= randomNumbers
			context.unbecome()
			while(!backlog.isEmpty && !buffer.isEmpty) {
				backlog.dequeue() ! buffer.dequeue()
			}
			preFetchIfAlmostEmpty()
	}

	private def preFetchIfAlmostEmpty() {
		if(buffer.size <= BatchSize / 4) {
			randomOrgClient ! FetchFromRandomOrg(BatchSize)
			context become receiveWhenWaiting
		}
	}

}

class RandomOrgRandom(randomOrgBuffer: ActorRef) extends Random {
	implicit val timeout = Timeout(10 seconds)

	override def next(bits: Int) = {
		if(bits <= 16) {
			random16Bits() & ((1 << bits) - 1)
		} else {
			(next(bits - 16) << 16) + random16Bits()
		}
	}

	private def random16Bits(): Int = {
		val future = randomOrgBuffer ? RandomRequest
		Await.result(future.mapTo[Int], 1 minute)
	}
}

