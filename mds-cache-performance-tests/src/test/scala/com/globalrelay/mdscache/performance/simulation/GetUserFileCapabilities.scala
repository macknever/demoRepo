package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.GET_USER_FILE_CAPABILITIES_FEEDER_PATH
import com.globalrelay.mdscache.performance.feeder.RandomUserCapabilitiesFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, GetUserFileCapabilitiesLoader}
import com.globalrelay.mdscache.performance.process.UserFileCapabilities
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class GetUserFileCapabilities extends Simulation{
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = UserFileCapabilities.getUserFileCapabilities

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_FILE_CAPABILITIES_FEEDER_PATH).random
    } else {
      RandomUserCapabilitiesFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("GetUserFileCapabilities")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.getUserFileCapabilitiesRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new GetUserFileCapabilitiesLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.getUserFileCapabilitiesRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.getUserFileCapabilitiesRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.getUserFileCapabilitiesDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.getUserFileCapabilitiesUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
