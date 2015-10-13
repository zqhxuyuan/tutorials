package bitzguild.scollection.function

import bitzguild.scollection._
import bitzguild.scollection.function._
import bitzguild.scollection.transform.LeftFunctionCache

object DoublesFunctions {
  def sma(domain: LeftSeq[Double], length: Int)   = new LeftFunctionCache(domain, new SimpleMovingAverage, length)
  def xma(domain: LeftSeq[Double], length: Int)   = new LeftFunctionCache(domain, new ExponentialMovingAverage(length), length)
  def dema(domain: LeftSeq[Double], length: Int)  = new LeftFunctionCache(domain, new DoublesSmoothedMovingAverage(length), length)
  def tema(domain: LeftSeq[Double], length: Int)  = new LeftFunctionCache(domain, new TripleSmoothedMovingAverage(length), length)

}