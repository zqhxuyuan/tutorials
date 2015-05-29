package com.zqh.akka.helloworld

/**
 * Created by hadoop on 15-2-2.
 */
import akka.actor._
import akka.routing.RoundRobinRouter

import scala.collection.mutable
import scala.collection.mutable.HashMap
import scala.concurrent.duration.{Duration, _}


/**
 * Created by yulinguo on 1/15/15.
 *
 * http://my.oschina.net/yulinguo/blog/368281
 *
 */
object WordCount extends App{

  //execute this method
  calcultate()

  //all messages
  sealed trait wcMessage
  case class FinalRes(du:Duration,res:String=null) extends wcMessage

  case object MapTask extends wcMessage
  case class Task(splitedData:Array[String]) extends wcMessage
  case class ReduceTask(partData:mutable.HashMap[String,Int]) extends wcMessage

  def calcultate() = {
    //create system actor
    val system = ActorSystem("WordCountwithakka")
    println("begin")

    //create listener
    val listener = system.actorOf(Props[Listener],name = "WCListener")
    println("listener inited")

    //create master
    val master= system.actorOf(Props(new Master(listener,4)),name="WCMaster")
    println("master inited")

    //begin to calculate 向master发送了map消息。master在接受到消息后则开始工作
    println("begin to calculate")
    master ! MapTask

  }

  // Listener负责返回输出结果以及关闭最后的系统
  class Listener extends Actor{
    override def receive= {
      case FinalRes(du,res) =>{
        println("\n\tCalculation time: \t%s".format(du))
        println("\n\tRes: \t%s".format(res))
        context.system.shutdown()
      }
    }
  }

  // Master总体来说负责分配任务，收集各个worker的工作结果并组装最后结果
  class Master(listener: ActorRef,numWorker : Int=4) extends Actor{
    //original data
    val data = "hello world hello this is my first akka app plz use scala Cause scala is really smart " +
      "I have used it so far in two real projects very successfully. both are in the near real-time traffic " +
      "information field (traffic as in cars on highways), distributed over several nodes, integrating messages"

    // 生成需要计算的word的data，然后根据worker的数目，计算每个worker需要处理的单词数目
    val dataArray =  data.split(" ")
    val sumSize = dataArray.size
    // 假设有4个worker, 上面的data有100个单词,则每个worker平均要处理25个单词
    val jobPerWork = sumSize/numWorker

    // 在分发任务的时候，使用worker router进行分发，分发的算法是轮训算法，每个节点都轮流进行计算
    val workerRouter = context.actorOf(Props[Worker].withRouter(RoundRobinRouter(numWorker)), name = "workerRouter")

    var totalRes = new mutable.HashMap[String,Int]
    var receivedMsg = 0
    val start: Long = System.currentTimeMillis

    def receive = {
      // Map：为接受到主函数的消息，接受到后则进行消息分发
      case MapTask =>{
        println("begin to map")
        for(i <- 1 to numWorker){ // 1, 2, 3, 4
          val sendData = dataArray.slice((i-1)*jobPerWork, i*jobPerWork)
          println("send worker "+i+" to work")
          workerRouter ! Task(sendData)
        }
      }
      // Reducer：接受到worker的计算结果后，进行合并。然后通知listener并关闭自己
      case ReduceTask(partData) =>{
        println("received one res")
        totalRes = mergeRes(totalRes,partData)

        receivedMsg = receivedMsg + 1
        if (receivedMsg == numWorker){
          println("all job finished")
          listener ! FinalRes(du = (System.currentTimeMillis - start).millis,totalRes.toList.toString())
          // Stops this actor and all its supervised children
          context.stop(self)
        }
      }
    }

    def mergeRes(allData:mutable.HashMap[String,Int],
                 partData:mutable.HashMap[String,Int]):mutable.HashMap[String,Int]={
      partData.foreach(a => {
        val numExisted = allData.getOrElse(a._1,0)
        val newNum = numExisted + a._2
        allData.put(a._1,newNum)
      })
      allData
    }
  }

  // worker负责统计工作的: 计算分配的数据中每个单词出现的次数，并返回给worker
  class Worker() extends Actor{

    def wordCount(data:Array[String]):HashMap[String,Int]={
      val wordcount = new mutable.HashMap[String,Int]()
      data.foreach(word =>{
        val count = wordcount.getOrElse(word,0)
        val newCount = count + 1
        wordcount.put(word,newCount)
      })
      wordcount
    }

    override def receive= {
      case Task(splitedData) =>{
        println("one to work")
        // sender表示task任务的发送方（也就是master）. 计算完毕后，给sender回复计算结果
        sender ! ReduceTask(wordCount(splitedData))
      }
    }
  }

}
