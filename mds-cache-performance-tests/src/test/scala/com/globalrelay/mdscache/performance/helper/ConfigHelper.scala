package com.globalrelay.mdscache.performance.helper

import com.typesafe.config.{Config, ConfigFactory}

import java.io.File

object ConfigHelper {
  val configPath: String = "config.path"

  val runDescription: String = "config.gatling.core.runDescription"

  val baseUrl: String = "config.simulation.baseUrl"
  val apiVersion: String = "config.simulation.apiVersion"

  val loadFactor: String = "config.simulation.loadFactor"
  val maxConnectionPerHost: String = "config.simulation.maxConnectionPerHost"

  val feederSize: String = "config.feeder.size"
  val useJson: String = "config.feeder.useJson"

  val wiremockEnabled: String = "config.wiremock.enabled"
  val wiremockScheme: String = "config.wiremock.scheme"
  val wiremockHost: String = "config.wiremock.host"
  val wiremockPort: String = "config.wiremock.port"

  val getVersionRate: String = "config.simulation.getVersion.rate"
  val getVersionRampDuring: String = "config.simulation.getVersion.rampDuring"
  val getVersionDuring: String = "config.simulation.getVersion.during"
  val getVersionUpperBoundForResponseTime: String = "config.simulation.getVersion.upperBoundForResponseTime"

  val getUserProfileRate: String = "config.simulation.getUserProfile.rate"
  val getUserProfileRampDuring: String = "config.simulation.getUserProfile.rampDuring"
  val getUserProfileDuring: String = "config.simulation.getUserProfile.during"
  val getUserProfileUpperBoundForResponseTime: String = "config.simulation.getUserProfile.upperBoundForResponseTime"
  val getUserProfileJsonFilename: String = "config.simulation.getUserProfile.jsonFilename"

  val getCompanyNumberRate: String = "config.simulation.getCompanyNumber.rate"
  val getCompanyNumberRampDuring: String = "config.simulation.getCompanyNumber.rampDuring"
  val getCompanyNumberDuring: String = "config.simulation.getCompanyNumber.during"
  val getCompanyNumberUpperBoundForResponseTime: String = "config.simulation.getCompanyNumber.upperBoundForResponseTime"
  val getCompanyNumberJsonFilename: String = "config.simulation.getCompanyNumber.jsonFilename"

  val getUserFileCapabilitiesRate: String = "config.simulation.getUserFileCapabilities.rate"
  val getUserFileCapabilitiesRampDuring: String = "config.simulation.getUserFileCapabilities.rampDuring"
  val getUserFileCapabilitiesDuring: String = "config.simulation.getUserFileCapabilities.during"
  val getUserFileCapabilitiesUpperBoundForResponseTime: String = "config.simulation.getUserFileCapabilities.upperBoundForResponseTime"
  val getUserFileCapabilitiesJsonFilename: String = "config.simulation.getUserFileCapabilities.jsonFilename"

  val viewDisclaimerRate: String = "config.simulation.viewDisclaimer.rate"
  val viewDisclaimerRampDuring: String = "config.simulation.viewDisclaimer.rampDuring"
  val viewDisclaimerDuring: String = "config.simulation.viewDisclaimer.during"
  val viewDisclaimerUpperBoundForResponseTime: String = "config.simulation.viewDisclaimer.upperBoundForResponseTime"
  val viewDisclaimerJsonFilename: String = "config.simulation.viewDisclaimer.jsonFilename"

  val getUserRate: String = "config.simulation.getUser.rate"
  val getUserRampDuring: String = "config.simulation.getUser.rampDuring"
  val getUserDuring: String = "config.simulation.getUser.during"
  val getUserUpperBoundForResponseTime: String = "config.simulation.getUser.upperBoundForResponseTime"
  val getUserJsonFilename: String = "config.simulation.getUser.jsonFilename"

  val resolveContactRate: String = "config.simulation.resolveContact.rate"
  val resolveContactRampDuring: String = "config.simulation.resolveContact.rampDuring"
  val resolveContactDuring: String = "config.simulation.resolveContact.during"
  val resolveContactUpperBoundForResponseTime: String = "config.simulation.resolveContact.upperBoundForResponseTime"
  val resolveContactJsonFilename: String = "config.simulation.resolveContact.jsonFilename"

  val multiEndpointsRate: String = "config.simulation.multiEndpoints.rate"
  val multiEndpointsRampDuring: String = "config.simulation.multiEndpoints.rampDuring"
  val multiEndpointsDuring: String = "config.simulation.multiEndpoints.during"
  val multiEndpointsUpperBoundForResponseTime: String = "config.simulation.multiEndpoints.upperBoundForResponseTime"

  val kafkaBrokers: String = "config.kafka.brokers"
  val kafkaSerializer: String = "config.kafka.serializer"
  val kafkaSecurityProtocol: String = "config.kafka.securityProtocol"
  val kafkaTruststoreLocation: String = "config.kafka.truststoreLocation"
  val kafkaPassword: String = "config.kafka.password"
  val kafkaMdsTopic: String = "config.kafka.mdsTopic"
  val kafkaUcsTopic: String = "config.kafka.ucsTopic"

  val evictHeadersRate: String = "config.simulation.evictHeaders.rate"
  val evictHeadersRampDuring: String = "config.simulation.evictHeaders.rampDuring"
  val evictHeadersDuring: String = "config.simulation.evictHeaders.during"
  val evictHeadersUpperBoundForResponseTime: String = "config.simulation.evictHeaders.upperBoundForResponseTime"

  val getMetricsScrapeInterval: String = "config.metrics.scrapeInterval"

  val mdsHeaderSetSizes: String = "config.headerSet.mds.sizes"
  val mdsHeaderSetDistributions: String = "config.headerSet.mds.distributions"
  val ucsHeaderSetSizes: String = "config.headerSet.ucs.sizes"
  val ucsHeaderSetDistributions: String = "config.headerSet.ucs.distributions"
  
  val mdsWildcardHeaderPresent: String = "config.headerSet.mds.wildcard.present"
  val ucsWildcardHeaderPresent: String = "config.headerSet.ucs.wildcard.present"
  val mdsWildcardHeaderRate: String = "config.headerSet.mds.wildcard.rate"
  val ucsWildcardHeaderRate: String = "config.headerSet.ucs.wildcard.rate"
  
  /**
   * Loads configurations from a file.
   *
   * The configuration is loaded in two stages:
   * 1. Internal Configuration: If a file name is provided via `fileOption`, 
   *    it loads the specified file; otherwise, it loads the default `application.conf`.
   * 2. External Configuration: Reads an additional configuration file specified 
   *    by the `config.path` property in the internal configuration.
   *
   * The external configuration takes precedence over the internal configuration 
   * but falls back to the internal configuration for missing values.
   *
   * @param fileOption an optional parameter specifying the name of the internal 
   *                   configuration file to load. Defaults to None, which loads 
   *                   `application.conf`.
   * @return a merged `Config` object containing values from both external and internal 
   *         configurations.
   */
  def getConfig(fileOption: Option[String] = None): Config = {
    val internalConfig = fileOption.fold(ifEmpty = ConfigFactory.load())(file => ConfigFactory.load(file))
    val externalConfig: Config = ConfigFactory.parseFile(new File(internalConfig.getString(ConfigHelper.configPath)))
    externalConfig.withFallback(internalConfig)
  }
}
