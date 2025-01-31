package com.globalrelay.mdscache.performance.process

import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import io.gatling.core.Predef.*
import io.gatling.core.session.Expression
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*

import java.net.URLEncoder

object Contact {
  val jwtToken: String = JwtUtilHolder.jwtUtil.getJwtToken

  def resolveContact: ChainBuilder = {
    // Session must contain id and number
    exec {
      http("ResolveContact")
        .post(session => {
          val id: String = URLEncoder.encode(session("id").as[String], "UTF-8")

          s"/users/$id/contacts/resolve"
        })
        .body(StringBody(getResolveContactBody))
        .header("Authorization", s"Bearer $jwtToken")
        .header("Content-type", "application/json")
        .check(status.is(200))
        .check(jsonPath("$.id").ofType[String].is("#{contactId}"))
    }
  }

  private val getResolveContactBody: Expression[String] = session => {
    val address: String = session("address").as[String]
    val addressType: String = session("addressType").as[String]
    val contactId: String = session("contactId").as[String]

    s"""{ "address": "$address", "addressType": "$addressType", "contactId": "$contactId" }"""
  }
}
