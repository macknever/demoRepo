package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.*
import com.globalrelay.mdscache.performance.feeder.RandomCompanyNumberFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, GetCompanyNumberLoader}
import com.globalrelay.mdscache.performance.process.CompanyNumber
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class GetCompanyNumber extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = CompanyNumber.getCompanyNumber

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_COMPANY_NUMBER_FEEDER_PATH).random
    } else {
      RandomCompanyNumberFeeder.DATA.random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("GetCompanyNumber")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.getCompanyNumberRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new GetCompanyNumberLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.getCompanyNumberRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.getCompanyNumberRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.getCompanyNumberDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.getCompanyNumberUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
