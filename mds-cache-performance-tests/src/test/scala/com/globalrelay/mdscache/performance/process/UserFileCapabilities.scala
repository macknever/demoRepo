package com.globalrelay.mdscache.performance.process

import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*

import java.net.URLEncoder

object UserFileCapabilities {
  val jwtToken: String = JwtUtilHolder.jwtUtil.getJwtToken
  
  def getUserFileCapabilities: ChainBuilder = {
    // Session must contain id 
    exec {
      http("GetFileCapabilities")
        .get(session => {
          val id: String = URLEncoder.encode(session("id").as[String], "UTF-8")
          
          s"/users/$id/file-capabilities"
        })
        .header("Authorization", s"Bearer $jwtToken")
        .check(status.is(200))
    }
  }
}
