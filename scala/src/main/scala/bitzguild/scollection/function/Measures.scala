package bitzguild.scollection.function

import bitzguild.scollection.LeftSeq
import bitzguild.scollection.transform._

/**
 * Highest value in the domain scoping length.
 */
class Highest extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = domain.max
}

/**
 * Lowest value in the domain scoping length.
 */
class Lowest extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = domain.min
}

/**
 * Sum of elements over look back period
 */
class SumOverPeriod extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = domain.sum
}

/**
 * Difference between first and last element of period.
 * Result indicates amount of movement up or down over period.
 *
 * @param length look back
 */
class Momentum(length: Int) extends LeftDoublesFunction {
  val len = length -1
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = domain(0) - domain(len)
}

/**
 * Efficiency Ratio is a measure of signal to noise for a given series.
 * Specifically, the ratio captures directionality of the underlying series.
 *
 * @param length look back period
 */
class EfficiencyRatio(val length: Int) extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = {
    val signal = Math.abs(domain(0) - domain(length-1))
    // val noise = domain.zip(domain.tail).foldLeft(0.0)((s,t) => s + Math.abs(t._2 - t._1))
    var noise = 0.0
    for(i <- 0 until length-1) noise += Math.abs(domain(i)-domain(1))
    if (noise == 0.0) 1.0 else Math.abs(signal/noise)
  }
}

/**
 * Standard Deviation function with an optional center point. The custom center point
 * enables more accurate or faster-tracking averages to be used in the calculation.
 * Default is to use the XMA (Exponential Moving Average) of the given length.
 *
 * @param length look back period
 * @param centerFn [optional] smoothing function
 */
class StandardDeviation(val length: Int, centerFn: Option[LeftDoublesFunction] = None) extends LeftDoublesFunction {
  def sumSquares(domain: LeftSeq[Double], center: LeftSeq[Double]) : Double =
    domain.zip(center).map(t => t._1 - t._2).map(x => x*x).foldLeft(0.0)(_+_)
  var center : LeftSeq[Double] = null   
  def init(domain: LeftSeq[Double]) = 
    center = new LeftFunctionCache(domain, centerFn.getOrElse(new ExponentialMovingAverage(length)), length) 
  def apply(domain: LeftSeq[Double]) = sumSquares(domain, center)/length
}


object Measures {
  
  def mean(d: Seq[Double]) = if (d.size == 0) 0.0 else d.sum / d.size
  def median(d: Seq[Double]) : Double = {
    val size = d.size
    if (size > 1) {
	    val s = d.sorted
	    val mid = size / 2
	    if (size % 2 == 1) s(mid) else (s(mid) + s(mid-1))/2
    } else if (size > 0) d(0)
    else 0.0
  }
  
  def efficiency(domain: LeftSeq[Double], length: Int) = new LeftFunctionCache(domain, new EfficiencyRatio(length), length)
}