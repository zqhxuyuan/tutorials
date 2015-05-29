package com.zqh.akka.queue

import akka.actor.{ActorLogging, Actor, ActorSystem, Cancellable, Props, ActorRef, PoisonPill, Terminated}
import akka.routing.{RoundRobinRouter, Broadcast, FromConfig}
import scala.collection.mutable.ArrayBuffer
import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

case class Customer(name: String, arrived: Long, processDuration: Long)

case object Start
case object Stop

class TubeStation extends Actor with ActorLogging {
	import context._
	private case object NewCustomer
	private case object TimerBeep

	private var scheduler: Cancellable = _
	private var customerCount = 0
	private var machineRouter: ActorRef = _
	private var customers = new ArrayBuffer[CustomerServiced]()
	// This is a sample of 20 process durations
	private val sampleDurations = ArrayBuffer(8043, 12308, 36047, 146671, 155191, 39570, 103982, 195214, 35533, 11971, 139932, 59791, 36952, 64827, 83796, 20263, 115964, 10883, 24533, 11967)
	// This is a sample of 10 customer inter-arrivals
	private val sampleIntervals = ArrayBuffer(9896, 2230, 13853, 22139, 16476, 4730, 19479, 34878, 14512, 5774)

	// Load configuration
	var conf = ConfigFactory.load
	lazy val useSampleDuration = conf.getString("queue-sample.use-sample-duration")
	lazy val customerArrivalFrequency = conf.getDouble("queue-sample.avg-customer-arrival-rate")
	lazy val useSampleArrival = conf.getString("queue-sample.use-sample-arrival")

	// Initialization
	override def preStart(): Unit = {
		// Nothing to do here
	}

	// Stop scheduler on actor shutdown
	override def postStop(): Unit = {
		if (!scheduler.isCancelled) { scheduler.cancel() }
		// Some stats
		log.info(s"Terminating TubeStation. Some stats: ${customers.length} customers served:")
		var totalWaitTime = 0L
		for(elt <- customers) {
			totalWaitTime += (elt.startService - elt.arrived)
			log.info(s"${elt.name}: wait time: ${elt.startService - elt.arrived}, serviced in: ${elt.endService - elt.startService} by ${elt.actorPath}")
		}
		log.info(s"Average wait time: ${totalWaitTime / customers.length}")
	}

	// Timer function
	def startTimer = {
		// Get next inter-arrival delay
		val nextInterval = {
			// Either pre-sampled inter-arrivals or dynamically generated one
			if (useSampleArrival == "on") {
				// This might not be fully thread-safe...
				val index: Long = Utilities.rebase(customerCount, sampleIntervals.length)
				sampleIntervals(index.toInt)
			} else {
				Utilities.poissonRandomInterarrival(customerArrivalFrequency) * 1000
			}
		}
		log.info(s"Next customer #${customerCount + 1} in $nextInterval ms")
		scheduler = context.system.scheduler.scheduleOnce (nextInterval milliseconds, self, NewCustomer)
	}

	// Message loop
	def receive = {
		case Start =>
			log.info("Opening Tube Station")
			// Create a router as per deployment configuration
			machineRouter = system.actorOf(Props[TicketMachine].withRouter(FromConfig()), "machineRouter")
			context.watch(machineRouter)
			log.info("Tickets machines available")
			// Start scheduler on actor startup
			startTimer
		case Stop =>
			scheduler.cancel()
			machineRouter ! Broadcast(PoisonPill)
			log.info("Closing Tube Station. No further clients accepted")
		case NewCustomer =>
			customerCount += 1
			// If we want sample durations (as opposed to dynamically created ones), then use the ones in the array sampleDurations
			val duration = {
				if (useSampleDuration == "on") {
					val index: Long = Utilities.rebase(customerCount - 1, sampleDurations.length)
					sampleDurations(index.toInt)
				} else {
					// 0 means we want a dynamically generated duration
					0
				}
			}
			// If you want to pass e.g.: a fix process duration, pass it as third param otherwise leave it to 0
			val cust = new Customer(s"Customer #$customerCount", System.currentTimeMillis, duration)
			log.info(s"New customer joining: ${cust.name}, with processing duration: $duration ms")
			machineRouter ! cust
			// Next Scheduler
			startTimer
		case CustomerServiced(name, arrived, startService, endService, actorPath) =>
			customers += new CustomerServiced(name, arrived, startService, endService, actorPath)
		case Terminated(corpse) =>
			if (corpse == machineRouter) {
				log.info("Terminating")
				context.system.shutdown()
			}
		case _ =>
			log.info("Whatchagummy?")
	}
}