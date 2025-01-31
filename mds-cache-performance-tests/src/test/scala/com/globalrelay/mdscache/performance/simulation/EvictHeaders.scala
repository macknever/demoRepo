package com.globalrelay.mdscache.performance.simulation

import com.globalrelay.mdscache.performance.Predef.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.*
import com.globalrelay.mdscache.performance.feeder.MixedFeeder
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.loader.*
import com.globalrelay.mdscache.performance.metrics.KafkaProcessingMetricsUtil
import com.globalrelay.mdscache.performance.metrics.action.LogKafkaMetricActionBuilder
import com.globalrelay.mdscache.performance.process.KafkaEvictHeader
import com.globalrelay.mdscache.performance.protocol.KafkaProtocolBuilder
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.feeder.*
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class EvictHeaders extends Simulation {

  val config: Config = ConfigHelper.getConfig()

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(BASE_URL)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val brokers: String = config.getString(ConfigHelper.kafkaBrokers)
  val serializer: String = config.getString(ConfigHelper.kafkaSerializer)
  val securityProtocol: String = config.getString(ConfigHelper.kafkaSecurityProtocol)
  val trustStoreLocation: String = config.getString(ConfigHelper.kafkaTruststoreLocation)
  val password: String = config.getString(ConfigHelper.kafkaPassword)

  var kafkaProtocol: KafkaProtocolBuilder = kafka
    .broker(brokers)
    .securityProtocol(securityProtocol, trustStoreLocation, password)
    .producerKeySerializer(serializer)
    .producerValueSerializer(serializer)

  val metricsUtil: KafkaProcessingMetricsUtil = KafkaProcessingMetricsUtil.getInstance()
  val scrapeInterval: Int = config.getInt(ConfigHelper.getMetricsScrapeInterval)
  /* This delay is required because of the Prometheus' scrape interval. For example, if the last scrape happens right
      before pushing ends, the simulation must wait for another scrape interval to get updated metrics */
  val delay: Int = config.getInt(ConfigHelper.evictHeadersRampDuring) + config.getInt(ConfigHelper.evictHeadersDuring) +
    scrapeInterval
  // delay must be in ms
  metricsUtil.setDelayMs(delay * 1000)

  val sendMdsEvictHeaders: ChainBuilder = KafkaEvictHeader.sendMixedMdsEvictHeader
  val sendUcsEvictHeaders: ChainBuilder = KafkaEvictHeader.sendMixedUcsEvictHeader

  private def getMdsFeeder: FeederBuilderBase[Any] = {
    MixedFeeder.MIXED_MDS_DATA.collect(headers => Map[String, Any]("evict" -> headers)).random
  }

  private def getUcsFeeder: FeederBuilderBase[Any] = {
    MixedFeeder.MIXED_UCS_DATA.collect(headers => Map[String, Any]("evict" -> headers)).random
  }

  var warmUpMdsEvictHeader: ScenarioBuilder = scenario("send mds evict header warm up")
    .feed(getMdsFeeder())
    .exec(
      sendMdsEvictHeaders
    )

  var warmUpUcsEvictHeader: ScenarioBuilder = scenario("send ucs evict header warm up")
    .feed(getUcsFeeder())
    .exec(
      sendUcsEvictHeaders
    )

  var mdsEvictHeader: ScenarioBuilder = scenario("send mds evict header")
    .feed(getMdsFeeder())
    .exec(
      sendMdsEvictHeaders
    )

  var ucsEvictHeader: ScenarioBuilder = scenario("send ucs evict header")
    .feed(getUcsFeeder())
    .exec(
      sendUcsEvictHeaders
    )

  val scnLog: ScenarioBuilder = scenario("LogKafkaProcessingEvents")
    .exec(new LogKafkaMetricActionBuilder(metricsUtil, scrapeInterval * 1000))

  val load: Double = config.getDouble(ConfigHelper.evictHeadersRate) * config.getDouble(ConfigHelper.loadFactor)
  val upperBound: Int = config.getInt(ConfigHelper.evictHeadersUpperBoundForResponseTime)
  val totalRequests: Long = config.getInt(ConfigHelper.evictHeadersRate) * config.getInt(ConfigHelper.evictHeadersRampDuring)
   + config.getInt(ConfigHelper.evictHeadersRate) * config.getInt(ConfigHelper.evictHeadersDuring) * 2

  before {
    val companyLoader: DataLoader = new GetCompanyNumberLoader
    val userLoader: DataLoader = new GetUserLoader
    val userFileCapabilitiesLoader: DataLoader = new GetUserFileCapabilitiesLoader
    val userProfileLoader: DataLoader = new GetUserProfileLoader
    val viewDisclaimerLoader: DataLoader = new ViewDisclaimerLoader
    val resolveContactLoader: DataLoader = new ResolveContactLoader

    companyLoader.load()
    userLoader.load()
    userFileCapabilitiesLoader.load()
    userProfileLoader.load()
    viewDisclaimerLoader.load()
    resolveContactLoader.load()

    metricsUtil.initialize()
  }

  setUp(
    warmUpMdsEvictHeader.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.evictHeadersRampDuring)
    ),
    warmUpUcsEvictHeader.inject(
      rampUsersPerSec(0).to(load)
        during config.getInt(ConfigHelper.evictHeadersRampDuring)
    ),
    mdsEvictHeader.inject(
      nothingFor(config.getInt(ConfigHelper.evictHeadersRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.evictHeadersDuring)
    ),
    ucsEvictHeader.inject(
      nothingFor(config.getInt(ConfigHelper.evictHeadersRampDuring)),
      constantUsersPerSec(load)
        during config.getInt(ConfigHelper.evictHeadersDuring)
    ),
    scnLog.inject(
      // wait for additional 5 seconds for the extreme case
      nothingFor(delay + 5),
      constantUsersPerSec(1).during(1),
    )
  ).assertions(
    // Include wait period
    global.responseTime.percentile3.lt(upperBound),
    global.failedRequests.count.is(0),
    global.allRequests.count.is(totalRequests * 2)
  ).protocols(kafkaProtocol, httpProtocol)

}
