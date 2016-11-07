package example

import java.util.concurrent.{ConcurrentHashMap, LinkedBlockingQueue}

/**
 * https://github.com/shirdrn/scala-learning
 */
sealed trait Event

sealed trait UserEvent extends Event

case class DownloadEvent(code: Int, channel: String, imsi: String, timestamp: Long) extends UserEvent
case class InstallEvent(code: Int, udid: String, channel: String, mac: String, imei: String, idfa: String, idfv: String, timestamp: Long) extends UserEvent
case class LaunchEvent(code: Int, udid: String, sessionId: String, ip: Long, timestamp: Long) extends UserEvent
case class PlayEvent(code: Int, udid: String, sessionId: String, ip: Long, duration: Long, timestamp: Long) extends UserEvent
case class ClickEvent(code: Int, udid: String, sessionId: String, ip: Long, pageId: Int, timestamp: Long) extends UserEvent

sealed trait AdEvent extends Event

case class AdClickEvent(code: Int, udid: String, adid: String, timestamp: Long) extends AdEvent
case class AdExposureEvent(code: Int, udid:String, adid: String, timestamp: Long) extends AdEvent

trait EventHandler {
  def handle[E](event: E): Unit
}

trait EventBus[H <: AnyRef, E] {
  def register(eventType: Class[_ <: E], eventSubTypes: Seq[Class[_ <: E]], handler: H): Unit
  def post(event: E): Unit = notifyEventSource()
  def start(): Unit
  protected def notifyEventSource(): Unit
}

abstract class AbstractEventBus[H <: AnyRef, E] extends EventBus[H, E] {

  protected val handlers = new ConcurrentHashMap[Class[_ <: E], Option[H]]
  protected val eventTypes = new ConcurrentHashMap[Class[_ <: E], Class[_ <: E]]

  def register(eventType: Class[_ <: E], eventSubTypes: Seq[Class[_ <: E]], handler: H): Unit = {
    handlers.put(eventType, Some(handler))
    println("Main event type registered: (" + eventType + " -> " + handler + ")")
    for(subType <- eventSubTypes) {
      eventTypes.put(subType, eventType)
      println("Sub event type registered: (" + eventType + " -> " + subType + ")")
    }
  }

  protected def dispatch(event: E): Unit = {
    val handler = handlers.get(eventTypes.get(event.getClass))
    onHandle(handler, event)
  }

  protected def onHandle(handler: Option[H], event: E): Unit
}

trait AsynchronousEventBus[H <: EventHandler, E <: Event] extends AbstractEventBus[H, E] {

  private val EVENT_QUEUE_CAPACITY = 1000
  private val eventQueue = new LinkedBlockingQueue[E](EVENT_QUEUE_CAPACITY)
  private val eventReceiver = new Thread("RECV") {
    override def run(): Unit = {
      while(true) {
        val event = eventQueue.poll
        if(event != null) {
          dispatch(event)
          println("dispatch(): event=" + event)
        } else {
          Thread.sleep(1000)
        }
      }
    }
  }

  def start(): Unit = {
    eventReceiver.start()
  }

  abstract override def post(event: E): Unit = {
    eventQueue.offer(event)
    println("post(): event=" + event)
    // notify event source
    super.post(event)
  }
}

class AppEventBus extends AsynchronousEventBus[EventHandler, Event] {

  protected def notifyEventSource(): Unit = {
    println("Event bus notify: event received!")
  }

  protected def onHandle(handler: Option[EventHandler], event: Event): Unit = {
    handler.foreach(_.handle(event))
    println("onHandle(): handler=" + handler + ", event=" + event)
  }
}

class UserEventHandler(name: String) extends EventHandler {

  def handle[UserEvent](event: UserEvent): Unit = {
    event match {
      case DownloadEvent(code, channel, imsi, timestamp) => println((code, channel, imsi, timestamp))
      case InstallEvent(code, udid, channel, mac, imei, idfa, idfv, timestamp) => println((code, udid, channel, mac, imei, idfa, idfv, timestamp))
      case LaunchEvent(code, udid, sessionId, ip, timestamp) => println((code, udid, sessionId, ip, timestamp))
      case PlayEvent(code, udid, sessionId, ip, duration, timestamp) => println((code, udid, sessionId, ip, duration, timestamp))
      case ClickEvent(code, udid, sessionId, ip, pageId, timestamp) => println((code, udid, sessionId, ip, pageId, timestamp))
      case _ => println("Unknown user event")
    }
  }

}

class AdEventHandler(name: String) extends EventHandler {

  def handle[AdEvent](event: AdEvent): Unit = {
    event match {
      case AdClickEvent(code, udid, adid, timestamp) => println(code)
      case AdExposureEvent(code, udid, adid, timestamp) => println(code)
      case _ => println("Unknown ad event")
    }

  }
}

object EventProcessingSystem {

  def main(args: Array[String]): Unit = {
    val eventBus = new AppEventBus()
    eventBus.register(classOf[UserEvent],
      Seq[Class[_ <: Event]](classOf[DownloadEvent], classOf[InstallEvent], classOf[LaunchEvent], classOf[PlayEvent], classOf[ClickEvent]),
      new UserEventHandler("UEH"))
    eventBus.register(classOf[AdEvent],
      Seq[Class[_ <: Event]](classOf[AdClickEvent], classOf[AdExposureEvent]),
      new AdEventHandler("AEH"))
    eventBus.start()

    eventBus.post(DownloadEvent(12003, "A-baidu", "A00000371E4685", System.currentTimeMillis()))
    eventBus.post(AdClickEvent(19001, "fd901acb87710ac", "baidu9018", System.currentTimeMillis()))
  }

}
