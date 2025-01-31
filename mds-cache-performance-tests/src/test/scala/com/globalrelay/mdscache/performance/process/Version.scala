package com.globalrelay.mdscache.performance.process

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object Version {
  def getVersion: ChainBuilder = {
    val ret: ChainBuilder = exec {
      http("GetVersion")
        .get("/version")
        .check(status.is(200))
        .check(jsonPath("$.version").exists)
    }
    ret
  }
}
