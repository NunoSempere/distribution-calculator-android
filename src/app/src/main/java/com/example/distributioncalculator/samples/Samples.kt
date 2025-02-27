package com.example.distributioncalculator.samples

import kotlin.random.Random
import kotlin.math

val NORMAL90CONFIDENCE = 1.6448536269514727

// Simple sampling
fun SampleUnitNormal(): Double {
  val u1 = Random.nextDouble()
  val u2 = Random.nextDouble()
  z = math.sqrt(-2.0 * math.ln(u1)) * math.sin(2 * math.PI * u2);
  return z
}


fun SampleNormal(mean: Double, std: Double): Double {
  return mean + sigma*SampleUnitNormal()
}

fun SampleNormalFrom90CI(low: Double, high: Double) Double {
    val mean = (high + low) / 2.0;
    val std = (high - low) / (2.0 * NORMAL90CONFIDENCE);
    return sample_normal(mean, std, seed);
}

fun SampleTo(low: Double, high: Double) Double {
  val loglow = math.ln(low)
  val loghigh = math.ln(high)
  val result = math.exp(SampleNormalFrom90CI(loglow, lowghigh))
  return result
}

// Classes

sealed class Distribution {
    data class Lognormal(val low: Double, val high: Double) : Distribution()
    data class SamplesArray(val samples: DoubleArray) : Distribution()
}

fun lognormalToSamples(l: Distribution.Lognormal): Distribution.SamplesArray {
  return Distribution.SamplesArray(DoubleArray(100_000) { SampleTo(l.low, l.high) })
}

fun multiplyLogDists(l1: Distribution.Lognormal, l2: Distribution.Lognormal): Distribution.Lognormal {
  val logmean1 := (ln(l1.low) + ln(l1.high)) / 2.0
  val logstd1  := (ln(l1.low) - ln(l1.high)) / 2.0

  val logmean1 := (ln(l2.low) + ln(l2.high)) / 2.0
  val logstd1  := (ln(l2.low) - ln(l2.high)) / 2.0

	val logmean_product = logmean1 + logmean2
	val logstd_product = sqrt(logstd1*logstd1 + logstd2*logstd2)

	val h = logstd_product * NORMAL90CONFIDENCE
	val loglow  = logmean_product - h
	val loghigh = logmean_product + h

	val newlow = exp(loglow)
	val newhigh = exp(loghigh)

	return Distribution.Lognormal(low = newlow, high = newhigh)
}

fun multiplySamplesArray(xs: Distribution.SamplesArray, ys: Distribution.SamplesArray) {

  val zs = DoubleArray(xs.samples.size)

  for (i in array1.samples.indices) {
      zs [i] = xs.samples[i] * ys.samples[i]
  }
  return Distribution.SamplesArray(zs)
}

fun MultiplyDists(d1: Distribution, d2: Distribution): Distribution {
  when {
    d1 is Distribution.Lognormal    && d2 is Distribution.Lognormal -> multiplyLogDists(d1, d2)
    d1 is Distribution.Lognormal && d2 is Distribution.SamplesArray -> multiplySamplesArray(lognormalToSamples(d1), d2)
    d1 is Distribution.SamplesArray && d2 is Distribution.Lognormal -> multiplySamplesArray(d1, lognormalToSamples(d2))
    d1 is Distribution.SamplesArray && d2 is Distribution.SamplesArray -> multiplySamplesArray(d1, d2)
  }
}
