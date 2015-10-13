package bitzguild.scollection.mutable

import scala.collection.generic.CanBuildFrom
import scala.collection.{immutable, GenSeq}
import scala.collection.mutable.ArrayBuffer
import bitzguild.scollection.{LeftView, MutableLeftSeq}

object LeftSeries {

}


// ------------------------------------------------------------------------------------
// LeftSeq Implementations
// ------------------------------------------------------------------------------------

/**
 * Common behavior and data between different LeftView implementations
 *
 * @param parent source data
 * @param lookback supported history
 * @param marker relative store in parent
 * @tparam A element type
 */
abstract class LeftInnerView[A](parent: MutableLeftSeq[A], lookback: Int, marker : Int) extends LeftView[A] {
  protected var csize = marker
  def another = parent.another
  def length = lookback
  def view(lookback: Int) = parent.view(lookback)
  def firstView(lookback: Int) = parent.firstView(lookback)
  override def toString = if(parent.size == 0) "LeftView()" else super.toString
  override def size = lookback
}



/**
 * Fixed-size recycling storage buffer. Grows only by appending elements. Index zero represents
 * last element added and positive indices from zero access earlier elements.
 *
 * @param capacity fixed capacity
 * @tparam A element type
 */
class LeftRing[A](val capacity: Int = 5) extends MutableLeftSeq[A] {

  class LeftRingView[A](parent: LeftRing[A], lookback: Int, cursor: Int) extends LeftInnerView[A](parent,lookback,cursor) {
    def data = parent.data
    def apply(i: Int) = data((csize + i) % data.size)
    def next = if (hasNext) { csize = csize-1; this } else this
    def hasNext = {
      if (parent.size == 0) true
      else (csize != parent.cursor)
    }
  }

  protected val cmax = (Int.MaxValue / capacity) * capacity - capacity
  protected var idx = cmax
  protected def cursorToLeft(i: Int)	= if (i == 0) cmax else i - 1
  protected def assignAndShift(elem: A) = { idx = cursorToLeft(idx); data(idx % capacity) = elem }
  protected def cursor = idx

  val data = new ArrayBuffer[A]()
  def length = data.length
  def apply(i: Int) = data((idx + i) % data.size)
  def +=(elem: A): this.type = {
    if (size < capacity) {
      for (i <- 1 to capacity) data += elem
    }
    else assignAndShift(elem)
    this
  }
  def ++=(col: Traversable[A]) : this.type = { col.foreach(e => this += e); this }
  def another = new LeftRing[A](capacity)
  def view(lookback: Int) = new LeftRingView(this,lookback,cursor)
  def firstView(lookback: Int) = new LeftRingView(this,lookback,cmax)
}





/**
 * Variable size array. Grows only by appending elements. Index zero represents
 * last element added and positive indices from zero access earlier elements.
 *
 * @tparam A element type
 */
class LeftArray[A]() extends MutableLeftSeq[A] {

  class LeftArrayView[A](parent: LeftArray[A], val offset: Int, lookback: Int, capturesize : Int) extends LeftInnerView[A](parent,lookback,capturesize) {
    def apply(index: Int) = parent(Math.max(0,Math.min(parent.size-1,parent.size - csize + index)))
    def hasNext = (csize != parent.size)
    def next = if (hasNext) { csize = csize+1; this } else this
  }

  val arrdata = new collection.mutable.ArrayBuffer[A]()
  def length = arrdata.length
  override def size = arrdata.size
  def apply(i: Int) = arrdata((size - i - 1) % size)
  def +=(elem: A): this.type = {
    arrdata += elem
    this
  }
  def ++=(col: Traversable[A]) : this.type = { col.foreach(e => this += e); this }
  def another = new LeftArray[A]()
  def view(lookback: Int) = new LeftArrayView(this,0,lookback,arrdata.size)
  def firstView(lookback: Int) = new LeftArrayView(this,0,lookback,0)
}


