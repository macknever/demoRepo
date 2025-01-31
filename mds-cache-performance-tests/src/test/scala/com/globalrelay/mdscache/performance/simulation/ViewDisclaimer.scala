package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.*
import com.globalrelay.mdscache.performance.feeder.RandomDisclaimerFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, ViewDisclaimerLoader}
import com.globalrelay.mdscache.performance.process.Disclaimer
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class ViewDisclaimer extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = Disclaimer.viewDisclaimer

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(VIEW_DISCLAIMER_FEEDER_PATH).random
    } else {
      RandomDisclaimerFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("ViewDisclaimer")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.viewDisclaimerRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new ViewDisclaimerLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.viewDisclaimerRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.viewDisclaimerRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.viewDisclaimerDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.viewDisclaimerUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
