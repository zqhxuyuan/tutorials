package bitzguild.scollection.transform

import bitzguild.scollection.LeftSeq

abstract class LeftDoublesFunction extends LeftSeqFunction[Double, LeftSeq[Double]] {
  def init(domain: LeftSeq[Double]) : Unit
  def apply(domain: LeftSeq[Double]): Double 
}
