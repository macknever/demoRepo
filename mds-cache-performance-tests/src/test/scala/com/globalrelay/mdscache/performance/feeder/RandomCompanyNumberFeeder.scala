package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper.{feederSize, getConfig}
import org.apache.commons.lang3.RandomStringUtils

object RandomCompanyNumberFeeder {
  val DATA: Array[Map[String, Any]] = Array.fill(getConfig().getInt(feederSize)) {
    Map(
      "id" -> rand.between(1, Int.MaxValue),
      "number" -> "+".concat(RandomStringUtils.randomNumeric(11)))
  }
}
