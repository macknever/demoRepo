package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.process.Version
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class GetVersion extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = Version.getVersion

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .group("WarmUp") {
      chain
    }
  val scn: ScenarioBuilder = scenario("GetVersion")
    .group("GetVersion") {
      chain
    }

  val load: Double = config.getDouble(ConfigHelper.getVersionRate) * config.getDouble(ConfigHelper.loadFactor)

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.getVersionRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.getVersionRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.getVersionDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.getVersionUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)

}
