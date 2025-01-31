package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper._

/**
 * Generates a set of random smIds.
 */
object RandomUserProfileFeeder {
  // object is lazily initialized in Scala
  val DATA: Array[Int] = Array.fill(getConfig().getInt(feederSize))(rand.between(1, Int.MaxValue))
}
