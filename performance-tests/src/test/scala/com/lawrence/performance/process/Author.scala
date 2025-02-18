package com.lawrence.performance.process

import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*

object Author {
  def postAuthor: ChainBuilder = {
    exec {
      http("PostAuthor")
        .post(session => {
          val topic: String = session("topic").as[String]

          s"/api/messages/$topic"
        })
        .header("Content-type", "application/json")
        .body(
          StringBody(session => {
            val id = session("id").as[String]
            val name = session("name").as[String]
            val personalName = session("personalName").as[String]

            s"""{
              "id": "$id",
              "name": "$name",
              "personalName": "$personalName"
            }"""
          })
        )
        .check(status.is(200))
    }
  }
}
