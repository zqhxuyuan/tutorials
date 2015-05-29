package com.zqh.akka.helloworld

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.dispatch.ExecutionContexts._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._

/**
 * Created by hadoop on 15-2-2.
 *
 * http://hongbinzuo.github.io/2014/12/16/Akka-Tutorial-with-Code-Conncurrency-and-Fault-Tolerance/
 *
 * 统计一个文本文件中单词的数量:
 * 父actor会从文件中装载每一行，然后委托一个子actor来计算某一行的单词数量。
 * 当子actor完成之后，它会把结果用消息发回给父actor。
 * 父actor会收到（每一行的）单词数量的消息并维持一个整个文件单词总数的计数器，这个计数器会在完成后返回给调用者。
 */
object WordCounter extends App{

  case class ProcessStringMsg(string: String)
  case class StringProcessedMsg(words: Integer)

  /**
   * 子Actor
   * 接收ProcessStringMsg消息（包含一行文本），计算这行文本中单词的数量，
   * 并把结果通过一个StringProcessedMsg消息返回给发送者。
   */
  class StringCounterActor extends Actor {
    def receive = {
      // ==> 5. 子Actor接收到父Actor发送的消息. WordCounterActor向StringCounterActor发送了ProcessStringMsg消息,参数为一行数据
      case ProcessStringMsg(string) => {
        // 发送到这个Actor接收到的消息内容, 求解这一行的单词个数
        val wordsInLine = string.split(" ").length
        // 这里的sender引用就是父Actor: WordCounterActor
        // ==> 6. 子Actor计算完毕, 将结果封装到StringProcessedMsg消息中, 向父Actor发送
        sender ! StringProcessedMsg(wordsInLine)
      }
      case _ => println("Error: message not recognized")
    }
  }

  // 开始处理文件的消息
  case class StartProcessFileMsg()

  /**
   * 父Actor
   * @param filename 文件名
   */
  class WordCounterActor(filename: String) extends Actor {
    private var running = false
    private var totalLines = 0
    private var linesProcessed = 0
    private var result = 0
    private var fileSender: Option[ActorRef] = None

    def receive = {
      // ==> 3. 从最初启动WordCounterActor的外部actor接收到的消息
      case StartProcessFileMsg() => {
        // 收到这个消息之后，WordCounterActor首先检查它收到的是不是一个重复的请求
        // 如果这个请求是重复的，那么WordCounterActor生成一个警告，然后就不做别的事了
        if (running) {
          // println just used for example purposes;
          // Akka logger should be used instead
          println("Warning: duplicate start message received")
        } else {
          running = true
          // WordCounterActor在fileSender实例变量中保存发送者的一个引用(ActorRef)
          // 当处理最终的StringProcessedMsg时，为了以后的访问和响应，这个ActorRef是必需的
          // ＝＝＞Note: 这个sender是main函数的调用者, 因为在main函数中[发送者],发送了StartProcessFileMsg消息给当前这个父Actor[接收者]
          fileSender = Some(sender) // save reference to process invoker
          import scala.io.Source._
          // WordCounterActor读取文件，当文件中每行都装载之后，(这里是一个循环, 每一行对应一个子Actor)
          fromFile(filename).getLines.foreach { line =>
            // 创建一个StringCounterActor，需要处理的包含行文本的消息就会传递给StringCounterActor(子Actor)
            // ==> 4. 父Actor读取文件的每一行,将每一行封装成ProcessStringMsg消息,发送给子Actor
            context.actorOf(Props[StringCounterActor]) ! ProcessStringMsg(line)
            // 一个子Actor只处理一行内容. 所以上面每一行都创建一个子Actor.
            totalLines += 1
          }
        }
      }
      // 当子Actor处理完成分配给它的行之后，从StringCounterActor处接收到的消息
      // ==> 7. 子Actor处理完成,结果就是words, 并向父Actor发送StringProcessedMsg消息
      // 父Actor收到子Actor的处理结果,进行汇总, 因为父Actor会将一个文件分成多行, 也就是有多个子Actor
      case StringProcessedMsg(words) => {
        // 进行结果汇总
        result += words
        linesProcessed += 1
        // 把文件的行计数器增加，如果所有的行都处理完毕, 它会把最终结果发给原来的fileSender
        if (linesProcessed == totalLines) {
          // ==> 8. fileSender就是客户端即main函数的调用引用.　父Actor向fileSender发送这个文件的单词个数的计算结果
          fileSender.map(_ ! result)  // provide result to process invoker
        }
      }
      case _ => println("message not recognized!")
    }
  }

  implicit val ec = global

  // Client API. Your Job Process
  override def main(args: Array[String]) {
    val system = ActorSystem("System")
    // ==> 1. 创建父Actor(Root Actor), 初始化实例对象接收一个文件名
    val actor = system.actorOf(Props(new WordCounterActor(args(0))))
    implicit val timeout = Timeout(25 seconds)

    // ==> 2. 向父Actor发送消息: 准备处理文件
    // 使用?,调用者可以使用返回的Future对象
    val future = actor ? StartProcessFileMsg()

    // ==> 9. 当完成之后可以打印出最后结果并最终通过停掉Actor系统退出程序。
    future.map { result =>
      println("Total number of words " + result)
      system.shutdown
    }
  }

}
