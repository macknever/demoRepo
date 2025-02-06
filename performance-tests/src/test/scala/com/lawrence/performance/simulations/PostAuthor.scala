package com.lawrence.performance.simulations

import com.lawrence.performance.helper.ConfigHelper
import com.lawrence.performance.process.Author
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.*
import io.gatling.http.protocol.HttpProtocolBuilder

class PostAuthor extends Simulation {
  val config: Config = ConfigHelper.getConfig()
  val baseUrl: String = config.getString(ConfigHelper.baseUrl)

  val httpProtocol: HttpProtocolBuilder = http.baseUrl(baseUrl)
    .disableCaching
    .maxConnectionsPerHost(config.getInt(ConfigHelper.maxConnectionPerHost))
    .shareConnections

  val chain: ChainBuilder = Author.postAuthor





}
