package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.*
import com.globalrelay.mdscache.performance.feeder.RandomUserProfileFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, GetUserProfileLoader}
import com.globalrelay.mdscache.performance.process.UserProfile
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class GetUserProfile extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = UserProfile.getUserProfile

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_PROFILE_FEEDER_PATH).random
    } else {
      RandomUserProfileFeeder.DATA.collect(smId => Map[String, Any]("smId" -> smId)).random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("GetUserProfile")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.getUserProfileRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new GetUserProfileLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.getUserProfileRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.getUserProfileRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.getUserProfileDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.getUserProfileUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
