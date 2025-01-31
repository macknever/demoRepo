package com.globalrelay.mdscache.performance.process

import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*

import java.net.URLEncoder

object CompanyNumber {
  val jwtToken: String = JwtUtilHolder.jwtUtil.getJwtToken

  def getCompanyNumber: ChainBuilder = {
    // Session must contain id and number
    exec {
      http("GetCompanyNumber")
        .get(session => {
          val id: Int = session("id").as[Int]
          val number: String = URLEncoder.encode(session("number").as[String], "UTF-8")

          s"/company/$id/number/$number"
        })
        .header("Authorization", s"Bearer $jwtToken")
        .check(status.is(200))
        .check(jsonPath("$.number").ofType[String].is("#{number}"))
    }
  }

}
