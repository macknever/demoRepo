package com.lawrence.performance.simulations

import com.lawrence.performance.feeder.RandomAuthorFeeder
import com.lawrence.performance.helper.ConfigHelper
import com.lawrence.performance.process.Author
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class PostAuthor extends Simulation {
  val config: Config = ConfigHelper.getConfig()
  val baseUrl: String = config.getString(ConfigHelper.baseUrl)

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(baseUrl)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val feeder = RandomAuthorFeeder.getData("data/author.txt").random

  val chain: ChainBuilder = Author.postAuthor

  val scn: ScenarioBuilder = scenario("not empty")
    .feed(feeder)
    .exec(chain)

  setUp(
    scn.inject(constantUsersPerSec(1000) during 10)
  ).protocols(httpProtocol)


}
