package com.zqh.akka.helloworld

import java.io.{File, RandomAccessFile}
import java.nio.channels.FileChannel

import akka.actor._
import akka.routing.{Broadcast, RoundRobinRouter}
import com.typesafe.config.ConfigFactory

/**
 * Created by hadoop on 15-2-3.
 *
 * http://www.dzone.com/links/r/word_count_map_reduce_with_akka_scala.html
 */
object WordCountMR extends App {

  val receiverSystem = ActorSystem("receiversystem", ConfigFactory.load("application_remote"))

  val c = receiverSystem.actorOf(Props[FileReceiver], "receiver")
  c ! "/home/hadoop/nohup.out"

  // 1 GB file

  class FileReceiver extends Actor {
    def receive = {
      case fileName: String =>
        val system = ActorSystem("receiversystem")
        val global = system.actorOf(Props(new CountAggregator(8)))

        val localAggregator = system.actorOf(Props(new LocalAggregator(global)))
        val lineCollector = system.actorOf(Props(new LineCollector(localAggregator)))

        val router = system.actorOf(Props(new LineCollector(localAggregator)).withRouter(RoundRobinRouter(nrOfInstances = 8)))
        print(s"Started at ==>")
        println(System.currentTimeMillis())

        // determine line boundaries for number of chunks
        val file = new File(fileName)
        val chunkSize = 10000
        val count = file.length() / chunkSize

        for (i <- 0 to count.intValue()) {
          val start = i * chunkSize //0, 10,20
          val end = chunkSize + start // 10,20,30
          router !(fileName, start, end) //send chunks
        }

        val remaining = chunkSize * count
        router !(fileName, remaining, file.length()) //send out remaining chunk

        router ! Broadcast(true) //broadcast end of day message!
    }
  }

  /**
   * Chunk processor
   */
  class LineCollector(localAgg: ActorRef) extends Actor {
    def receive = {

      case (fileName: String, chunkStart: Int, chunkSize: Int) =>

        val file = new File(fileName)
        val channel = new RandomAccessFile(file, "r").getChannel();
        val mappedBuff = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length()) //map complete file

        // load only if it is not loaded!

        var endP = chunkSize
        // file size is greater than chunk
        if (endP >= file.length()) {
          endP = file.length().intValue - 1
        }

        if (chunkStart < file.length()) {
          var start = mappedBuff.get(chunkStart) // start character
          val startPosition = trail(chunkStart, mappedBuff, start, endP)

          var end = mappedBuff.get(endP) // end character

          val endPosition = if ((endP != file.length() - 1)) trail(endP, mappedBuff, end, endP) else endP // valid end character
          val stringBuilder = new StringBuilder(endPosition - startPosition)
          val size = endPosition - startPosition
          val byteArray = new Array[Byte](size)

          // prepare and send buffer to local combiner
          if (endPosition > startPosition) {
            for (i <- startPosition to endPosition) {
              val character = mappedBuff.get(i).asInstanceOf[Char]
              if (character == '\n') {
                stringBuilder.append(' ')
              } else {
                stringBuilder.append(character)
              }
            }
            localAgg ! stringBuilder.toString.split(" ").groupBy(x => x) //sending chunks
          }

        }

      case (done: Boolean) =>
        localAgg ! done // end of day message
    }

    private def trail(startP: Int, charBuff: java.nio.MappedByteBuffer, start: Byte, length: Int): Int = {

      var s = start.asInstanceOf[Char]
      val position = startP
      var next = position

      // if start character is not space, keep backtracking to start with new character word
      if (position <= length) {
        while (!s.equals(' ') && position > 0) {
          s = charBuff.get(next).asInstanceOf[Char]
          next = next - 1
        }
      }

      if (position != next) next + 1 else position
    }

  }

  /**
   * Local(to line chunk collector) akka combiner
   */
  class LocalAggregator(globalAgg: ActorRef) extends Actor {
    val wordCountMap = scala.collection.mutable.Map[String, Int]()

    def receive = {
      case countMap: Map[String, Array[String]] =>
        countMap map { case (k, v) => wordCountMap += ((k, wordCountMap.getOrElse(k, 0) + v.size))}
      case complete: Boolean =>
        globalAgg ! wordCountMap
    }
  }

  /**
   * Global combiner to combine and print final output after aggregating results from local akka based combiners.
   */
  class CountAggregator(threadCount: Int) extends Actor {
    val wordCountMap = scala.collection.mutable.Map[String, Int]()
    var count: Integer = 0;

    def receive = {
      case localCount: scala.collection.mutable.Map[String, Int] =>
        //        count = count + 1
        localCount map (x => wordCountMap += ((x._1, wordCountMap.getOrElse(x._1, 0) + x._2)))
        count = count + 1
        if (count == threadCount) {
          println("Got the completion message ... khallas!")
          onCompletion
        }
    }

    // print final word count on output
    def onCompletion() {

      for (word <- wordCountMap.keys) {
        print(word + "=>")
        println(wordCountMap.get(word).get)
      }
      print(s"Completed at ==>")
      println(System.currentTimeMillis())

    }

  }

}
