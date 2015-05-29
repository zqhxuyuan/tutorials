package com.zqh.akka.queue

object Utilities {

	// Poisson time interval distribution
	def poissonRandomInterarrival(rate: Double): Double = {
		return -(Math.log(1.0 - Math.random()) / rate)
	}

	// This one is used to recalculate a value from base 10 to a new one
	// Very useful to rebase an index on a specific range
	def rebase(base10: Long, whatBase: Long): Long = {
		// Use of rint is to round without using BigDecimal
		((math rint (((base10.toDouble / whatBase.toDouble) % 1) * whatBase) *100) / 100).toLong
		// You can test it like that: (0 to 50) map {Utilities.rebase(_, 20)}
	}
}
