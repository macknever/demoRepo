package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper.{feederSize, getConfig}

object RandomDisclaimerFeeder {
  // object is lazily initialized in Scala
  val DATA: Array[Int] = Array.fill(getConfig().getInt(feederSize))(rand.between(1, Int.MaxValue))
}
