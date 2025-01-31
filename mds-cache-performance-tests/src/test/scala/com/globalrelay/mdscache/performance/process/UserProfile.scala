package com.globalrelay.mdscache.performance.process

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef._

object UserProfile {
  
  def getUserProfile: ChainBuilder = {
    // Session must contain smId 
    exec {
      http("GetUserProfile")
        .get("/user/#{smId}/profile")
        .check(status.is(200))
        .check(jsonPath("$.smId").ofType[Int].is("#{smId}"))
    }
  }
}
