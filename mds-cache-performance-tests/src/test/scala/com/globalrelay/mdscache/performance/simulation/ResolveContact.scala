package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.*
import com.globalrelay.mdscache.performance.feeder.RandomContactFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.{DataLoader, ResolveContactLoader}
import com.globalrelay.mdscache.performance.process.Contact
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class ResolveContact extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = Contact.resolveContact

  private def getFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(RESOLVE_CONTACT_FEEDER_PATH).random
    } else {
      RandomContactFeeder.DATA.random
    }
  }

  val warmUp: ScenarioBuilder = scenario("WarmUp")
    .feed(getFeeder())
    .exec(chain)

  val scn: ScenarioBuilder = scenario("ResolveContact")
    .feed(getFeeder())
    .exec(chain)

  val load: Double = config.getDouble(ConfigHelper.resolveContactRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val loader: DataLoader = new ResolveContactLoader

    loader.load()
  }

  setUp(
    warmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.resolveContactRampDuring)
    ),
    scn.inject(
      nothingFor(config.getInt(ConfigHelper.resolveContactRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.resolveContactDuring)
    )
  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.resolveContactUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
