package com.globalrelay.mdscache.performance.feeder

import com.globalrelay.mdscache.performance.helper.ConfigHelper.{feederSize, getConfig}
import org.apache.commons.lang3.RandomStringUtils

object RandomContactFeeder {
  val DATA: Array[Map[String, Any]] = Array.fill(getConfig().getInt(feederSize)) {
    Map(
      "id" -> "gr:".concat(RandomStringUtils.randomNumeric(6)),
      "address" -> "+".concat(RandomStringUtils.randomNumeric(11)),
      "addressType" -> "SMS",
      "contactId" -> "CT20A690E7D8C24B33B1A42C3F747D9ED9")
  }
}
