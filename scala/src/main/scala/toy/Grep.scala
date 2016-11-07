package toy

import java.io.File

import scala.collection.mutable
import scala.io.Source
import scala.sys.process._

/**
  * Created by zhengqh on 16/1/4.
  */
object Grep {

  ///home/qihuang.zheng/data/md5_id
  var file = "/Users/zhengqh/test/data"

  def main(args: Array[String]) {
    val start = System.currentTimeMillis()
    file = args(0)
    val folder = file.substring(0,file.lastIndexOf("/"))
    val fileName = file.substring(file.lastIndexOf("/")+1,file.length)
    val target = folder + s"/${fileName}_1"

    val numbers = List("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f")
    val maps = mutable.Map[String,mutable.Buffer[String]]()
    Source.fromFile(file, "utf8").getLines().foreach(line=>{
      val bufferOpt =  maps.get(line.substring(0,4))
      val buffer = bufferOpt.getOrElse(mutable.Buffer[String]())
      buffer += line
      maps += line.substring(0,4) -> buffer
    })

    val notExists = mutable.Buffer[String]()
    maps.foreach(row=>{
      val key = row._1
      for(l <- row._2){
        val result = s"cat $key" #| s"grep $l" #>> new File(target) !

        if(result == 1){
          notExists += l
        }
      }
    })
    val end = System.currentTimeMillis()
    println("query cost:" + (end-start)/1000)
    println("未命中的记录:\n")
    notExists.foreach(println)
  }
}
