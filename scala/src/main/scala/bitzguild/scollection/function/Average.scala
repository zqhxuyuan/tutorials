package bitzguild.scollection.function

import bitzguild.scollection.LeftSeq
import bitzguild.scollection.mutable.LeftRing
import bitzguild.scollection.transform._

/**
 * Simple Moving Average
 */
class SimpleMovingAverage extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = {}
  def apply(domain: LeftSeq[Double]) = domain.sum / domain.size
}

/**
 * Simple Moving Average (with rolling sum)
 */
class SimpleMovingAverageR extends LeftDoublesFunction {
  var sum = 0.0
  var prior = 0.0
  def init(domain: LeftSeq[Double]) = {
    sum = domain.sum
    prior = domain(domain.size-1)
  }
  def apply(domain: LeftSeq[Double]) = {
    sum = sum + domain(0) - prior
    sum / domain.size
  }
}

/**
 * Exponential Moving Average
 */
class ExponentialMovingAverage(val length: Int) extends LeftDoublesFunction {
  val alpha : Double = 2.0 / (1 + length)
  var prior = 0.0
  def init(domain: LeftSeq[Double]) = { prior = domain(0) }
  def apply(domain: LeftSeq[Double]) = (alpha * (domain(0) - prior)) + prior
}

/**
 * Double Smoothed Exponential Moving Average (a.k.a. Dema). Member of
 * the so-called 'zero-lag' averages, which counter lag effect through
 * some mathematical adjustment.
 * <br>
 * <code>(2 * XAverage(Price, Len)) - (XAverage(XAverage(Price, Len), Len))</code>
 */
class DoublesSmoothedMovingAverage(val length: Int) extends LeftDoublesFunction {
  var xma : LeftSeq[Double] = null
  var xma2 : LeftSeq[Double] = null
  def init(domain: LeftSeq[Double]) = {
    xma = DoublesFunctions.xma(domain, length)
    xma2 = DoublesFunctions.xma(xma, length)
  }
  def apply(domain: LeftSeq[Double]) = (2 * xma(0)) - xma2(0)  
}

/**
 * Triple Smoothed Exponential Moving Average (a.k.a. Tema). Member of
 * the so-called 'zero-lag' averages, which counter lag effect through
 * some mathematical adjustment.
 * <br>
 * <code>(3 * XAverage(Price, Len)) - (3 * XAverage(XAverage(Price, Len), Len)) + XAverage(XAverage(XAverage(Price, Len), Len), Len)</code>
 */
class TripleSmoothedMovingAverage(length: Int) extends DoublesSmoothedMovingAverage(length) {
  var xma3 : LeftSeq[Double] = null
  override def init(domain: LeftSeq[Double]) = {
    super.init(domain)
    xma3 = DoublesFunctions.xma(xma2, length)
  }
  override def apply(domain: LeftSeq[Double]) =  (3 * xma(0)) - (3 * xma2(0)) + xma3(0)
}

/**
 * Infinite Impulse Filter with compensated lag. This function
 * uses a fixed-length look back of three events. Reference is
 * July 2002 article in Technical Analysis of Stocks & Commodities,
 * by John F. Ehlers. The aim of so-called 'zero-lag' functions
 * is to counter effect of filtering lag through mathematical
 * adjustment.
 */
class InfiniteImpulseFilter3Pole(val length: Int) extends LeftDoublesFunction {
  var prior = 0.0
  def init(domain: LeftSeq[Double]) = { prior = domain(0) }
  def apply(domain: LeftSeq[Double]) = 0.2*(2.0*domain(0) - domain(3)) + 0.8*prior
}

/**
 * Infinite Impulse Filter with compensated lag. This function
 * uses a calculated N-period look back like the XMA. Reference is
 * July 2002 article in Technical Analysis of Stocks & Commodities,
 * by John F. Ehlers. The aim of so-called 'zero-lag' functions
 * is to counter effect of filtering lag through mathematical
 * adjustment.
 */
class InfiniteImpulseFilterNPole(val length: Int) extends LeftDoublesFunction {
  val alpha : Double = 2.0 / (1 + length)
  val lag : Int = ((1.0/alpha) - 1).toInt
  var prior = 0.0
  def init(domain: LeftSeq[Double]) = { prior = domain(0) }
  def apply(domain: LeftSeq[Double]) =  alpha*(2.0*domain(0) - domain(lag)) + (1.0 - alpha)*prior
}

/**
 * Finite Impulse Filter with compensated lag. This function
 * uses a fixed-length look back of five events. Reference is
 * July 2002 article in Technical Analysis of Stocks & Commodities,
 * by John F. Ehlers. The aim of so-called 'zero-lag' functions
 * is to counter effect of filtering lag through mathematical
 * adjustment.
 */
class FiniteImpulseFilter5Pole(val length: Int) extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = { }
  def apply(domain: LeftSeq[Double]) =  (domain(0) + 2.0*domain(1) + 3.0*domain(2) + 3.0*domain(3) + 2.0*domain(4) + domain(5))/12.0
}

/**
 * Finite Impulse Filter with compensated lag. This function
 * uses a fixed-length look back of six events. Reference is
 * July 2002 article in Technical Analysis of Stocks & Commodities,
 * by John F. Ehlers. The aim of so-called 'zero-lag' functions
 * is to counter effect of filtering lag through mathematical
 * adjustment.
 */
class FiniteImpulseFilter6Pole(val length: Int) extends LeftDoublesFunction {
  def init(domain: LeftSeq[Double]) = { }
  def apply(domain: LeftSeq[Double]) =  (domain(0) + 3.5*domain(1) + 4.5*domain(2) + 3*domain(3) + 0.5*domain(4) - 0.5*domain(5) - 1.5*domain(6))/10.5
}

/**
 * Second Order Moving Average as found in January 2000, TASC and adapted from
 * TradeStation implementation. This function aims to produce a moving average using the 
 * simple moving average (Sma), and an adjustment factor to compensate for lag error. 
 * The adjustment factor is based on applying the 2nd order derivative.
 */
class SecondOrderMovingAverage(val length: Int) extends LeftDoublesFunction {
  var sma : LeftSeq[Double] = null
  def init(domain: LeftSeq[Double]) = { sma = DoublesFunctions.sma(domain, length) }
  def apply(domain: LeftSeq[Double]) =  {
    var slope = 0.0
    var factor = 1.0
    for(i <- 0 until length) {
    	val ii = length - i - 1
    	factor = 1.0 + (2.0*i)
    	slope = slope + domain(ii)*((length - factor)/2.0)
    }
    sma(0) + (6.0 * slope)/((length + 1)*length)
  }
}

/**
 * Kaufman Adaptive Moving Average, an exponential
 * moving average with modulated smoothing. The
 * smoothing is exponential and based on notion
 * of price efficiency, or so-called signal-to-noise.
 * This measures the movement of price in one direction
 * vs. counter directions. When the efficiency ratio is
 * high, most price movement is in a single direction.
 * With high efficiency, the Kaufman AMA shortens the
 * effective average length and quickly adapts to price.
 * When efficiency is low the Kaufman AMA lengthens
 * the effective average length and price changes
 * have less impact. During these periods the average
 * will nearly flatten out.
 */
class KaufmanAdaptiveMovingAverage(val length: Int, val fast: Int, val slow: Int) extends LeftDoublesFunction {
  val alphaFast = 2.0 / (fast + 1)
  val alphaSlow = 2.0 / (slow + 1)
  var efficiency : LeftSeq[Double] = null
  var prior : Double = 0.0
  def init(domain: LeftSeq[Double]) = {
    efficiency = Measures.efficiency(domain, length)
    prior = domain(0)
  }
  def apply(domain: LeftSeq[Double]) = {
    val er = efficiency(0)
    val smooth = Math.pow(er*(alphaFast-alphaSlow), 2.0)
    prior + smooth*(domain(0) - prior)
  }
}

/**
 * Mesa Adaptive Moving Average.
 * Reference: ??? TASC, page ??; John F Ehlers.
 */
class MesaAdaptiveMovingAverage(val length: Int, fastLimit: Double = 0.5, slowLimit: Double = 0.1) extends LeftDoublesFunction {
  val twoPI = Math.PI*2.0
  val radians2degrees = 360.0/twoPI
  var alpha : Double = fastLimit
  var phase0 : Double = 0.0
  var phase1 : Double = 0.0
  var period0 : Double = 6.0
  var period1 : Double = 6.0
  var smoothPeriod : Double = 6.0
  var smoothPeriod0 : Double = smoothPeriod
  var smoothPeriod1 : Double = smoothPeriod
  var prior : Double = 0.0
  var re0 : Double = 0.0
  var im0 : Double = 0.0
  var re1 : Double = 0.0
  var im1 : Double = 0.0
  var smoothed : LeftRing[Double] = null
  var detrended : LeftRing[Double] = null
  var q1s : LeftRing[Double] = null
  var i1s : LeftRing[Double] = null
  var q2s : LeftRing[Double] = null
  var i2s : LeftRing[Double] = null
  var mama : LeftRing[Double] = null
  
  def quad(a: Double, b: Double, c: Double, d: Double) = 0.0962*a + 0.5769*b - 0.5769*c - 0.0962*c
  def init(domain: LeftSeq[Double]) = { 
    prior = domain(0)
    smoothed = new LeftRing[Double](length)
    detrended = new LeftRing[Double](length)
    q1s = new LeftRing[Double](length)
    i1s = new LeftRing[Double](length)
    q2s = new LeftRing[Double](length)
    i2s = new LeftRing[Double](length)
    mama = new LeftRing[Double](length)
  }
  def apply(domain: LeftSeq[Double]) = {
    val periodFactor = 0.075*period1 + 0.54
    val smooth = 4.0*domain(0) + 3.0*domain(1) + 2.0*domain(2) + domain(3)
    smoothed += smooth/10.0
    detrended += quad(smoothed(0),smoothed(2),smoothed(4),smoothed(6))*periodFactor
    
    // inphase and quadrature
    val i1 = detrended(3)
    val q1 = quad(detrended(0),detrended(2),detrended(4),detrended(6))*periodFactor
    i1s += i1
    q1s += q1
    
    // advance phase of i1 and q1 by 90 degrees
    val jI = quad(i1s(0), i1s(2), i1s(4), i1s(6))*periodFactor
    val jQ = quad(q1s(0), q1s(2), q1s(4), q1s(6))*periodFactor
    
    // phasor addition for 3-bar averaging
    val i2 = i1s(0) - jQ
    val q2 = q1s(0) + jI
    
    // smooth the I and Q components before applying discriminator
    i2s += (0.2*i2 + 0.8*i2s(1))
    q2s += (0.2*q2 + 0.8*q2s(1))
    
    // homodyne discriminator
    re0 = i2*i2s(1) + q2*q2s(1)
    im0 = i2*q2s(1) + q2*i2s(1)
    re0 = 0.2*re0 + 0.8*re1
	im0 = 0.2*im0 + 0.8*im1
	re1 = re0
	im1 = im0

	if (im0 != 0.0 && re0 < 0.0) period0 = twoPI/Math.atan(im0/re0)
	if (period0 > 1.5*period1) period0 = 1.5*period1
	if (period0 < 0.67*period1) period0 = 0.67*period1
	if (period0 < 6.0) period0 = 6.0
	if (period0 > 50.0) period0 = 50.0

	period0 = 0.2*period0 + 0.8*period1
	period1 = period0
	smoothPeriod0 = 0.33*period0 + 0.67*smoothPeriod1
	smoothPeriod1 = smoothPeriod0
	
	var deltaPhase = 0.0
	var alpha = 1.0
	
	if (i1 != 0.0) phase0 = radians2degrees*Math.atan(q1/i1)
	deltaPhase = phase1 - phase0
	phase1 = phase0
	
	if (deltaPhase < 1.0) deltaPhase = 1.0
	
	alpha = fastLimit / deltaPhase
	alpha = Math.max(slowLimit,Math.min(fastLimit,alpha))
	mama += alpha*domain(0) + (1 - alpha)*domain(1)
	
	0.5*alpha*mama(0) + (1-0.5*alpha)*mama(1)
  }
}

/**
 * Repeated Median Velocity. This calculation is less sensitive to
 * outliers than many other averages.
 */
class RepeatedMedianVelocity(val length: Int) extends LeftDoublesFunction {
  import scala.collection.mutable.ArrayBuffer
  def init(domain: LeftSeq[Double]) = { }
  def apply(domain: LeftSeq[Double]) = {
    val inner = new ArrayBuffer[Double]()
    val outer = new ArrayBuffer[Double]()
    for(i <- 0 until length) {
      for(j <- 0 until length) 
        if (i != j) inner += (domain(0) - domain(i)) / (i - j)
      outer += Measures.median(inner.sorted)
    }
    Measures.median(outer.sorted)
  }
}


