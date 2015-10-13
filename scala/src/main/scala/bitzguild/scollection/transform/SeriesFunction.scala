package bitzguild.scollection.transform

import bitzguild.scollection.{LeftSeq,LeftView}

trait LeftSeqFunction[A,LS >: LeftSeq[A]] {
  def init(domain: LS) : Unit
  def apply(domain: LS): A
}

/**
 * A LeftSeq collection which caches values for LeftSeqFunction with a given look back period.
 *  ike all LeftSeq, latest element is accessed with index zero. The LeftSeqCache storage is
 *  determined by the storage type of the input domain (via another), so that both domain and
 *  range match. For instance, if the domain is sparse storage, the range will also be sparse.
 *  If the domain is recycled (ring), the range will also recycle.
 *
 * @param domain input to cache function
 * @param cacheFunction series cache function
 * @param lookback number of history elements
 * @tparam A element type
 */
class LeftFunctionCache[A](
    domain : LeftSeq[A],
    cacheFunction: LeftSeqFunction[A, LeftSeq[A]],
    lookback : Int
    ) extends LeftSeq[A] {

  // Borrows from domain view + Syncs with parent function cache
  class LeftCacheView[A](val parent: LeftFunctionCache[A], val dview: LeftView[A]) extends LeftView[A] {
    def length = dview.length
    override def size = dview.size
    def next = { dview.next; this }
    def hasNext = dview.hasNext
    def apply(idx: Int): A = {
      parent.sync
      dview(idx)
    }
    def view(lookback: Int) = parent.view(lookback)
    def firstView(lookback: Int) = parent.firstView(lookback)
    def another = parent.another
    override def toString = if(parent.size == 0) "LeftCacheView()" else super.toString
  }

  val cache = domain.another
  var dview : Option[LeftView[A]] = initDomainView
  def initDomainView : Option[LeftView[A]] = {
    if (domain.size != 0) {
      val dv = domain.firstView(lookback)
      cacheFunction.init(dv)
      cache += domain.last  // ??? shouldn't this increment next?
      while(dv.hasNext) cache += cacheFunction(dv.next)
      Some(dv)
    } else None
  }
  def sync = {
    if (dview.isEmpty) dview = initDomainView
    val dv = dview.get
    while(dv.hasNext) cache += cacheFunction(dv.next)
  }
  def length = domain.length
  override def size = domain.size
  def apply(index: Int) = {
    sync
    cache(index)
  }
  def firstView(len: Int) = new LeftCacheView(this,cache.firstView(len))
  def view(len: Int) = new LeftCacheView(this,cache.view(len))
  def another = cache.another

  override def toString = {
    if (domain.size != 0 && cache.size == 0) cache += domain(0)
    if(cache.size == 0) "LeftFunctionCacheView()"
    else super.toString
  }

}

class LeftFunctionCombo[A](a: LeftSeq[A], b: LeftSeq[A], f: (A,A) => A) extends LeftSeq[A] {
  def length = Math.min(a.length,b.length)
  def apply(index: Int) = f(a(index),b(index))
  def another = a.another
  def view(len: Int) = null
  def firstView(len: Int) = null
}

