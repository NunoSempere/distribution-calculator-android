package com.example.distributioncalculator.samples

import kotlin.random.Random
import kotlin.math.*

val NORMAL90CONFIDENCE = 1.6448536269514727

// Simple sampling
fun SampleUnitNormal(): Double {
  val u1 = Random.nextDouble()
  val u2 = Random.nextDouble()
  val z = sqrt(-2.0 * ln(u1)) * sin(2 * PI * u2)
  return z
}

fun SampleNormal(mean: Double, std: Double): Double {
  return mean + std * SampleUnitNormal()
}

fun SampleNormalFrom90CI(low: Double, high: Double): Double {
    val mean = (high + low) / 2.0
    val std = (high - low) / (2.0 * NORMAL90CONFIDENCE)
    return SampleNormal(mean, std)
}

fun SampleTo(low: Double, high: Double): Double {
  val loglow = ln(low)
  val loghigh = ln(high)
  val result = exp(SampleNormalFrom90CI(loglow, loghigh))
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

// Multiplication
fun multiplyLogDists(l1: Distribution.Lognormal, l2: Distribution.Lognormal): Distribution.Lognormal {
  val logmean1 = (ln(l1.low) + ln(l1.high)) / 2.0
  val logstd1 = (ln(l1.high) - ln(l1.low)) / (2.0 * NORMAL90CONFIDENCE)

  val logmean2 = (ln(l2.low) + ln(l2.high)) / 2.0
  val logstd2 = (ln(l2.high) - ln(l2.low)) / (2.0 * NORMAL90CONFIDENCE)

  val logmean_product = logmean1 + logmean2
  val logstd_product = sqrt(logstd1*logstd1 + logstd2*logstd2)

  val h = logstd_product * NORMAL90CONFIDENCE
  val loglow = logmean_product - h
  val loghigh = logmean_product + h

  val newlow = exp(loglow)
  val newhigh = exp(loghigh)

  return Distribution.Lognormal(low = newlow, high = newhigh)
}

fun multiplySamplesArray(xs: Distribution.SamplesArray, ys: Distribution.SamplesArray): Distribution.SamplesArray {
  val zs = DoubleArray(xs.samples.size)

  for (i in xs.samples.indices) {
      zs[i] = xs.samples[i] * ys.samples[i]
  }
  return Distribution.SamplesArray(zs)
}

fun MultiplyDists(d1: Distribution, d2: Distribution): Distribution {
  return when {
    d1 is Distribution.Lognormal && d2 is Distribution.Lognormal -> multiplyLogDists(d1, d2)
    d1 is Distribution.Lognormal && d2 is Distribution.SamplesArray -> multiplySamplesArray(lognormalToSamples(d1), d2)
    d1 is Distribution.SamplesArray && d2 is Distribution.Lognormal -> multiplySamplesArray(d1, lognormalToSamples(d2))
    d1 is Distribution.SamplesArray && d2 is Distribution.SamplesArray -> multiplySamplesArray(d1, d2)
    else -> throw IllegalArgumentException("Unsupported distribution types") // TODO: how to catch this? 
  }
}


// Division

fun divideLogDists(l1: Distribution.Lognormal, l2: Distribution.Lognormal): Distribution.Lognormal {
  var inverse = Distribution.Lognormal(low = 1.0 / l2.high, high = 1.0/l2.low)
	return multiplyLogDists(l1, l2)
	// TODO: case where divide by zero
}


fun divideSamplesArray(xs: Distribution.SamplesArray, ys: Distribution.SamplesArray): Distribution.SamplesArray {
  val zs = DoubleArray(xs.samples.size)

  for (i in xs.samples.indices) {
      zs[i] = xs.samples[i] / ys.samples[i]
  }
  return Distribution.SamplesArray(zs)
}

fun DivideDists(d1: Distribution, d2: Distribution): Distribution {
  return when {
    d1 is Distribution.Lognormal && d2 is Distribution.Lognormal -> divideLogDists(d1, d2)
    d1 is Distribution.Lognormal && d2 is Distribution.SamplesArray -> divideSamplesArray(lognormalToSamples(d1), d2)
    d1 is Distribution.SamplesArray && d2 is Distribution.Lognormal -> divideSamplesArray(d1, lognormalToSamples(d2))
    d1 is Distribution.SamplesArray && d2 is Distribution.SamplesArray -> divideSamplesArray(d1, d2)
    else -> throw IllegalArgumentException("Unsupported distribution types") // TODO: how to catch this? 
  }
}

// Addition
fun sumSamplesArray(xs: Distribution.SamplesArray, ys: Distribution.SamplesArray): Distribution.SamplesArray {
  val zs = DoubleArray(xs.samples.size)

  for (i in xs.samples.indices) {
      zs[i] = xs.samples[i] + ys.samples[i]
  }
  return Distribution.SamplesArray(zs)
}

fun SumDists(d1: Distribution, d2: Distribution): Distribution {
  return when {
    d1 is Distribution.Lognormal && d2 is Distribution.Lognormal -> sumSamplesArray(lognormalToSamples(d1), lognormalToSamples(d2))
    d1 is Distribution.Lognormal && d2 is Distribution.SamplesArray -> sumSamplesArray(lognormalToSamples(d1), d2)
    d1 is Distribution.SamplesArray && d2 is Distribution.Lognormal -> sumSamplesArray(d1, lognormalToSamples(d2))
    d1 is Distribution.SamplesArray && d2 is Distribution.SamplesArray -> sumSamplesArray(d1, d2)
    else -> throw IllegalArgumentException("Unsupported distribution types") // TODO: how to catch this? 
  }
}


// Substraction
fun substractSamplesArray(xs: Distribution.SamplesArray, ys: Distribution.SamplesArray): Distribution.SamplesArray {
  val zs = DoubleArray(xs.samples.size)

  for (i in xs.samples.indices) {
      zs[i] = xs.samples[i] - ys.samples[i]
  }
  return Distribution.SamplesArray(zs)
}

fun SubstractDists(d1: Distribution, d2: Distribution): Distribution {
  return when {
    d1 is Distribution.Lognormal && d2 is Distribution.Lognormal -> substractSamplesArray(lognormalToSamples(d1), lognormalToSamples(d2))
    d1 is Distribution.Lognormal && d2 is Distribution.SamplesArray -> substractSamplesArray(lognormalToSamples(d1), d2)
    d1 is Distribution.SamplesArray && d2 is Distribution.Lognormal -> substractSamplesArray(d1, lognormalToSamples(d2))
    d1 is Distribution.SamplesArray && d2 is Distribution.SamplesArray -> substractSamplesArray(d1, d2)
    else -> throw IllegalArgumentException("Unsupported distribution types") // TODO: how to catch this? 
  }
}
