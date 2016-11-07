package example

/**
  * Created by zhengqh on 15/12/17.
  */
object GenData {

  import java.io._

  import scala.util.Random

  def printToFile(path: String)(op: PrintWriter => Unit) {
    val p = new PrintWriter(new File(path))
    try { op(p) } finally { p.close() }
  }

  def nextString = (1 to 10) map (_ => Random.nextPrintableChar) mkString
  def nextLine = (1 to 4) map (_ => nextString) mkString "\t"

  def main(args: Array[String]) {
    printToFile("tsv.data") { p =>
      for (_ <- 1 to 100000000) {
        p.println(nextLine)
      }
    }
  }
}
