package com.zqh.akka.WatchService

import akka.actor.Actor
import akka.event.{Logging, LoggingReceive}

/**
 * @author Tomasz Nurkiewicz
 * @since 3/30/13, 10:12 PM
 */
class FileSystemActor extends Actor {
	val log = Logging(context.system, this)
	val watchServiceTask = new WatchServiceTask(self)
	val watchThread = new Thread(watchServiceTask, "WatchService")

	override def preStart() {
		watchThread.setDaemon(true)
		watchThread.start()
	}

	override def postStop() {
		watchThread.interrupt()
	}

	def receive = LoggingReceive {
		case MonitorDir(path) => watchServiceTask watchRecursively path
		case Created(file) =>
		case Deleted(fileOrDir) =>
	}
}