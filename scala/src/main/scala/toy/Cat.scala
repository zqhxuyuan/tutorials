package toy

import java.io.File

import scala.concurrent._
import scala.sys.process._
import concurrent.ExecutionContext.Implicits.global

import concurrent.duration._

/**
  * Created by zhengqh on 16/1/4.
  */
object Cat {

  val number = List("0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f")
  val folder = List("11","12","13","14","21","22","23","31","32","33","34","35","36","37","41","42","43","45","46","50")

  def main(args: Array[String]) {
    hdfs()
  }

  def cat(): Unit ={
    val cmds = collection.mutable.Buffer[String]()

    for(n1 <- number){
      for(n2 <- number){
        for(n3 <- number){
          for(n4 <- number){
            var cmd = "cat "
            val file = n1+n2+n3+n4
            for(f <- folder){
              cmd += f + "/" + file + " "
            }
            cmd += ">" + file
            cmds += cmd
          }
        }
      }
    }

    val cmdFutureList:  List[Future[Int]] = cmds.map(cmd => {
      future {
        //如果命令中有重定向,不能直接执行命令: "cat 11/0000 12/0000" #>> new File("0000") !
        //cmd !  //cat 11/0000 12/0000 >> 0000这样子不行的

        val redirectFile = cmd.split(">")(1)
        val catCommand = cmd.split(">")(0)
        catCommand #>> new File(redirectFile) !
      }
    }).toList

    val futures = Future.sequence(cmdFutureList)
    Await.result(futures, 24 hours)
  }

  //0000-ffff
  //hadoop fs -mkidr /user/tongdun/md5_id/0000
  //hadoop fs -put 11/0000 /user/tongdun/md5_id/0000/11_0000
  //hadoop fs -put 11/ffff /user/tongdun/md5_id/ffff/11_ffff
  def hdfs(): Unit ={
    for(n1 <- number){
      for(n2 <- number){
        for(n3 <- number){
          for(n4 <- number){
            val file = n1+n2+n3+n4

            s"hadoop fs -mkdir /user/tongdun/md5_id/$file"  !

            for(f <- folder){
              s"hadoop fs -put $f/$file /user/tongdun/md5_id/$file/${f}_$file" !
            }
          }
        }
      }
    }

    //多线程跑,引起Heap内存溢出
//    val failure = collection.mutable.Buffer[String]()
//
//    val cmdFutureList:  List[Future[Int]] = cmds.map(cmd => {
//      future {
//        val res = cmd !
//
//        if(res == 1) failure += cmd
//        res
//      }
//    }).toList
//
//    val futures = Future.sequence(cmdFutureList)
//    Await.result(futures, 24 hours)
//    println("failure cmd:")
//    for(f <- failure) println(f)
  }

}
