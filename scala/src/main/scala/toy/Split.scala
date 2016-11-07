package toy

import java.io.FileWriter

import scala.io.Source

/**
  * Created by zhengqh on 16/1/4.
  */
object Split {

  def main(args: Array[String]) {
    val file = "/home/yipu.si/data_11/data_11.txt"
    val outFile = "/home/qihuang.zheng/data/md5_id/data"
    var index = 0
    var out = new FileWriter(outFile, true)
    Source.fromFile(file).getLines().foreach(row => {
      index += 1
      if(index>150000){
        out.write(row+"\n")
      }
    })
    out.close()
  }
}
