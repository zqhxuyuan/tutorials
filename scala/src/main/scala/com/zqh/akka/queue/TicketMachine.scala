package com.zqh.akka.queue

import akka.actor.{ActorLogging, Actor}
import com.typesafe.config.ConfigFactory

case class CustomerServiced(name: String, arrived: Long, startService: Long, endService: Long, actorPath: String)

class TicketMachine extends Actor with ActorLogging {

	private var totalClientsServiced = 0L
	private var totalServicingTime = 0L
	private var totalIdleTime = 0L
	private var totalOperatingTime = 0L
	private var meanClientServiceTime = 0L
	private var startTime = 0L
	private var endTime = 0L
	private var efficiency = 0.0

	// Load configuration
	var conf = ConfigFactory.load
	//private val customerProcessingFrequency = 0.011f // One client every 1mn30 on average
	lazy val customerProcessingFrequency = conf.getDouble("queue-sample.avg-customer-processing-duration")

	// Initialization
	override def preStart(): Unit = {
		startTime = System.currentTimeMillis
	}

	// Termination
	override def postStop(): Unit = {
		endTime = System.currentTimeMillis
		totalOperatingTime = endTime - startTime
		totalIdleTime = totalOperatingTime - totalServicingTime
		if (totalClientsServiced > 0) { meanClientServiceTime = totalServicingTime / totalClientsServiced }
		if (totalOperatingTime > 0) { efficiency = 100.00 * totalServicingTime / totalOperatingTime }
		log.info(f"Ticket Machine ${self.path.name}%s terminating. Some stats: $totalClientsServiced%s clients serviced, $meanClientServiceTime%s service average duration, $totalServicingTime%s: servicing time out of $totalOperatingTime%s total operating time or $efficiency%2.2f %%")
	}

	def receive = {
		case Customer(name, arrived, processDuration) =>
			val start = System.currentTimeMillis
			// Get next Poisson time interval
			val interval:Long = {
				if (processDuration == 0) {
					// if client has passed its own process duration then use that, otherwise go fish one
					(Utilities.poissonRandomInterarrival(customerProcessingFrequency) * 1000).toLong
				} else {
					processDuration
				}
			}
			log.info(s"Start processing new request for $name. Will last $interval ms.")
			Thread.sleep(interval)
			log.info(s"Finished processing request for $name")
			// Stats
			totalClientsServiced += 1
			totalServicingTime += interval
			// Back to parent
			val cust = new CustomerServiced(name, arrived, start, System.currentTimeMillis, self.path.name)
			sender ! cust
		case _ =>
			log.info("Whatchagummy?")
	}

}