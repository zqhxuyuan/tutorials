package com.zqh.scala.stm

/**
 * Created by hadoop on 15-2-26.
 *
 * http://nbronson.github.io/scala-stm/quick_start.html
 */
import scala.concurrent.stm._

class ConcurrentIntList {
  private class Node(val elem: Int, prev0: Node, next0: Node) {
    val isHeader = prev0 == null
    val prev = Ref(if (isHeader) this else prev0)
    val next = Ref(if (isHeader) this else next0)
  }

  private val header = new Node(-1, null, null)

  def addLast(elem: Int) {
    atomic { implicit txn =>
      val p = header.prev()
      val newNode = new Node(elem, p, header)
      p.next() = newNode
      header.prev() = newNode
    }
  }

  def addLast(e1: Int, e2: Int, elems: Int*) {
    atomic { implicit txn =>
      addLast(e1)
      addLast(e2)
      elems foreach { addLast(_) }
    }
  }

  //def isEmpty = atomic { implicit t => header.next() == header }
  def isEmpty = header.next.single() == header

  def removeFirst(): Int = atomic { implicit txn =>
    val n = header.next()
    if (n == header)
      retry
    val nn = n.next()
    header.next() = nn
    nn.prev() = header
    n.elem
  }

  def maybeRemoveFirst(): Option[Int] = {
    atomic { implicit txn =>
      Some(removeFirst())
    } orAtomic { implicit txn =>
      None
    }
  }

  override def toString: String = {
    atomic { implicit txn =>
      val buf = new StringBuilder("ConcurrentIntList(")
      var n = header.next()
      while (n != header) {
        buf ++= n.elem.toString
        n = n.next()
        if (n != header) buf ++= ","
      }
      buf ++= ")".toString
      //buf.++=(")").toString()
      buf.toString
    }
  }
}

object ConcurrentIntList extends App{
  def select(stacks: ConcurrentIntList*): (ConcurrentIntList, Int) = {
    atomic { implicit txn =>
      for (s <- stacks) {
        s.maybeRemoveFirst() match {
          case Some(e) => return (s, e)
          case None =>
        }
      }
      retry
    }
  }

  var list = new ConcurrentIntList
  list.addLast(1)
  list.addLast(2)

}
