package com.globalrelay.mdscache.performance.config

import com.globalrelay.mdscache.performance.helper.ConfigHelper._

object ApiConfigs {
  val API_VERSION: String = getConfig().getString(apiVersion)

  val BASE_URL: String = getConfig().getString(baseUrl) + "/" + API_VERSION
}
