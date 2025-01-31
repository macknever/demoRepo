package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper.{feederSize, getConfig}
import org.apache.commons.lang3.RandomStringUtils

object RandomUserCapabilitiesFeeder {
  // object is lazily initialized in Scala
  val DATA: Array[String] = Array.fill(getConfig().getInt(feederSize))("gr:" + RandomStringUtils.randomNumeric(6))
}
