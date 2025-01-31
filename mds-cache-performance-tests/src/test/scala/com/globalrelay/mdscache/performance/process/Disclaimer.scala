package com.globalrelay.mdscache.performance.process

import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*

import java.net.URLEncoder

object Disclaimer {
  val jwtToken: String = JwtUtilHolder.jwtUtil.getJwtToken

  def viewDisclaimer: ChainBuilder = {
    // Session must contain id
    exec {
      http("ViewDisclaimer")
        .get("/company/#{id}/disclaimer")
        .header("Authorization", s"Bearer $jwtToken")
        .check(status.is(200))
    }
  }
}
