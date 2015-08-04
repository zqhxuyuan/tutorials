package com.zqh.akka.helloworld

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, ActorRef, Terminated}
import akka.io.{Tcp, IO}

/**
 * Created by zhengqh on 15/8/4.
 *
 * http://hseeberger.github.io/blog/2013/06/17/introduction-to-akka-i-slash-o/
 */
object EchoServerApp extends App{

  val system = ActorSystem("echo-service-system")
  val endpoint = new InetSocketAddress("localhost", 11111)
  system.actorOf(EchoService.props(endpoint), "echo-service")

  readLine(s"Hit ENTER to exit ...${System.getProperty("line.separator")}")
  system.shutdown()
}

object EchoService {
  def props(endpoint: InetSocketAddress): Props = Props(new EchoService(endpoint))
}
class EchoService(endpoint: InetSocketAddress) extends Actor with ActorLogging {
  import context.system

  IO(Tcp) ! Tcp.Bind(self, endpoint)

  override def receive: Receive = {
    case Tcp.Connected(remote, _) =>
      log.debug("Remote address {} connected", remote)
      sender ! Tcp.Register(context.actorOf(EchoConnectionHandler.props(remote, sender)))
  }
}

object EchoConnectionHandler {
  def props(remote: InetSocketAddress, connection: ActorRef): Props =
    Props(new EchoConnectionHandler(remote, connection))
}

class EchoConnectionHandler(remote: InetSocketAddress, connection: ActorRef) extends Actor with ActorLogging {
  // We need to know when the connection dies without sending a `Tcp.ConnectionClosed`
  context.watch(connection)

  def receive: Receive = {
    case Tcp.Received(data) =>
      val text = data.utf8String.trim
      log.debug("Received '{}' from remote address {}", text, remote)
      text match {
        case "close" => context.stop(self)
        case _       => sender ! Tcp.Write(data)
      }
    case _: Tcp.ConnectionClosed =>
      log.debug("Stopping, because connection for remote address {} closed", remote)
      context.stop(self)
    case Terminated(connection) =>
      log.debug("Stopping, because connection for remote address {} died", remote)
      context.stop(self)
  }
}