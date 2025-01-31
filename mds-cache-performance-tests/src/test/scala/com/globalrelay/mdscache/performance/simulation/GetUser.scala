package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.GET_USER_FEEDER_PATH
import com.globalrelay.mdscache.performance.feeder.RandomUserFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, GetUserLoader}
import com.globalrelay.mdscache.performance.process.User
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder


class GetUser extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = User.getUser

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_FEEDER_PATH).random
    } else {
      RandomUserFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("GetUser")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.getUserRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new GetUserLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.getUserRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.getUserRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.getUserDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.getUserUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
