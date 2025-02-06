package com.lawrence.performance.helper

import com.typesafe.config.{Config, ConfigFactory}

import java.io.File

object ConfigHelper {
  val configPath: String = "config.path"

  val runDescription: String = "config.gatling.core.runDescription"

  val baseUrl: String = "config.simulations.baseUrl" 
  val maxConnectionPerHost: String = "config.simulation.maxConnectionPerHost"


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
