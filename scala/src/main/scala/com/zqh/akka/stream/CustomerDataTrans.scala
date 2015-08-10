package com.zqh.akka.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Flow, Source}

import scala.util.Random

/**
 * Created by zqhxuyuan on 15-8-10.
 *
 * http://boldradius.com/blog-post/VS0NpTAAADAACs_E/introduction-to-akka-streams
 *
 */

object InputCustomer {
  def random():InputCustomer = {
    InputCustomer(s"FirstName${Random.nextInt(1000)} LastName${Random.nextInt(1000)}")
  }
}
case class InputCustomer(name: String)
case class OutputCustomer(firstName: String, lastName: String)

object CustomerDataTrans extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  //Source: 输入源
  val inputCustomers = Source((1 to 100).map(_ => InputCustomer.random()))

  //Flow: 规范化数据, 将输入格式转为输出格式. 要给Input包装上Flow
  val normalize = Flow[InputCustomer].map(c => c.name.split(" ").toList).collect {
    case firstName::lastName::Nil => OutputCustomer(firstName, lastName)
  }

  //Sink: 输出,同样也要给Output包装上Sink
  val writeCustomers = Sink.foreach[OutputCustomer] { customer =>
    println(customer)
  }

  //转换方法只是指定了转换的格式,所以要指定输入源, 并运行Flow, 其中runWith的参数为输出Sink
  //takes our input Source, passes it through the normalize Flow and then sends it to the writeCustomers Sink.
  //Source.via(transformation).runWith(Sink)
  inputCustomers.via(normalize).runWith(writeCustomers).andThen {
    case _ =>
      actorSystem.shutdown()
      actorSystem.awaitTermination()
  }
}
