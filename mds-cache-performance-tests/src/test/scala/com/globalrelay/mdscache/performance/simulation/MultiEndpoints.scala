package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.feeder.*
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.*
import com.globalrelay.mdscache.performance.process.*
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.Predef.Simulation
import io.gatling.core.feeder.FeederBuilderBase
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class MultiEndpoints extends Simulation {
  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val getUserProfileChain: ChainBuilder = UserProfile.getUserProfile
  val getCompanyNumberChain: ChainBuilder = CompanyNumber.getCompanyNumber
  val getUserFileCapabilitiesChain: ChainBuilder = UserFileCapabilities.getUserFileCapabilities
  val viewDisclaimerChain: ChainBuilder = Disclaimer.viewDisclaimer
  val getUserChain: ChainBuilder = User.getUser
  val resolveContactChain: ChainBuilder = Contact.resolveContact


  private def getUserProfileFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_PROFILE_FEEDER_PATH).random
    } else {
      RandomUserProfileFeeder.DATA.collect(smId => {
        Map[String, Any]("smId" -> smId)
      }).random
    }
  }

  private def getCompanyNumberFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_COMPANY_NUMBER_FEEDER_PATH).random
    } else {
      RandomCompanyNumberFeeder.DATA.random
    }
  }

  private def getUserFileCapabilitiesFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_FILE_CAPABILITIES_FEEDER_PATH).random
    } else {
      RandomUserCapabilitiesFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  private def viewDisclaimerFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(VIEW_DISCLAIMER_FEEDER_PATH).random
    } else {
      RandomDisclaimerFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  private def getUserFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(GET_USER_FEEDER_PATH).random
    } else {
      RandomUserFeeder.DATA.collect(id => Map[String, Any]("id" -> id)).random
    }
  }

  private def resolveContactFeeder: FeederBuilderBase[Any] = {
    val useJson: Boolean = config.getBoolean(ConfigHelper.useJson)

    if (useJson) {
      jsonFile(RESOLVE_CONTACT_FEEDER_PATH).random
    } else {
      RandomContactFeeder.DATA.random
    }
  }

  val getUserProfileWarmUp: ScenarioBuilder = scenario("GetUserProfile WarmUp")
    .feed(getUserProfileFeeder())
    .exec(getUserProfileChain)

  val getUserProfileScn: ScenarioBuilder = scenario("GetUserProfile")
    .feed(getUserProfileFeeder())
    .exec(getUserProfileChain)

  val getCompanyNumberWarmUp: ScenarioBuilder = scenario("GetCompanyNumber WarmUp")
    .feed(getCompanyNumberFeeder())
    .exec(getCompanyNumberChain)

  val getCompanyNumberScn: ScenarioBuilder = scenario("GetCompanyNumber")
    .feed(getCompanyNumberFeeder())
    .exec(getCompanyNumberChain)

  val getUserFileCapabilitiesWarmUp: ScenarioBuilder = scenario("GetUserFileCapabilities WarmUp")
    .feed(getUserFileCapabilitiesFeeder())
    .exec(getUserFileCapabilitiesChain)

  val getUserFileCapabilitiesScn: ScenarioBuilder = scenario("GetUserFileCapabilities")
    .feed(getUserFileCapabilitiesFeeder())
    .exec(getUserFileCapabilitiesChain)

  val viewDisclaimerWarmUp: ScenarioBuilder = scenario("ViewDisclaimer WarmUp")
    .feed(viewDisclaimerFeeder())
    .exec(viewDisclaimerChain)

  val viewDisclaimerScn: ScenarioBuilder = scenario("ViewDisclaimer")
    .feed(viewDisclaimerFeeder())
    .exec(viewDisclaimerChain)

  val getUserWarmUp: ScenarioBuilder = scenario("GetUser WarmUp")
    .feed(getUserFeeder())
    .exec(getUserChain)

  val getUserScn: ScenarioBuilder = scenario("GetUser")
    .feed(getUserFeeder())
    .exec(getUserChain)

  val resolveContactWarmUp: ScenarioBuilder = scenario("ResolveContact WarmUp")
    .feed(resolveContactFeeder())
    .exec(resolveContactChain)

  val resolveContactScn: ScenarioBuilder = scenario("ResolveContact")
    .feed(resolveContactFeeder())
    .exec(resolveContactChain)

  val load: Double = config.getDouble(ConfigHelper.multiEndpointsRate) * config.getDouble(ConfigHelper.loadFactor)

  before {
    val getUserProfileLoader: DataLoader = new GetUserProfileLoader
    val getCompanyNumberLoader: DataLoader = new GetCompanyNumberLoader
    val getUserFileCapabilitiesLoader: DataLoader = new GetUserFileCapabilitiesLoader
    val viewDisclaimerLoader: DataLoader = new ViewDisclaimerLoader
    val getUserLoader: DataLoader = new GetUserLoader
    val resolveContactLoader: DataLoader = new ResolveContactLoader

    getUserProfileLoader.load()
    getCompanyNumberLoader.load()
    getUserFileCapabilitiesLoader.load()
    viewDisclaimerLoader.load()
    getUserLoader.load()
    resolveContactLoader.load()
  }

  setUp(
    getUserProfileWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    getUserProfileScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    ),
    getCompanyNumberWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    getCompanyNumberScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    ),
    getUserFileCapabilitiesWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    getUserFileCapabilitiesScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    ),
    viewDisclaimerWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    viewDisclaimerScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    ),
    getUserWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    getUserScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    ),
    resolveContactWarmUp.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.multiEndpointsRampDuring)
    ),
    resolveContactScn.inject(
      nothingFor(config.getInt(ConfigHelper.multiEndpointsRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.multiEndpointsDuring)
    )

  ).assertions(
    global.responseTime.percentile3.lt(config.getInt(ConfigHelper.multiEndpointsUpperBoundForResponseTime)),
    global.failedRequests.count.is(0)
  ).protocols(httpProtocol)
}
