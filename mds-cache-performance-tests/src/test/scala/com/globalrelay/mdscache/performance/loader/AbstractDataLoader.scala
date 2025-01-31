package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.helper.ConfigHelper.getConfig
import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import com.globalrelay.nucleus.wiremock.WireMockSession

abstract class AbstractDataLoader extends DataLoader {
  val useJson: Boolean = getConfig().getBoolean(ConfigHelper.useJson)
  val wireMockEnabled: Boolean = getConfig().getBoolean(ConfigHelper.wiremockEnabled)

  val jwtToken: String = JwtUtilHolder.jwtUtil.getJwtToken

  override def load(): Unit = {
    if (useJson) {
      loadJson()
    } else {
      loadRandomData()
    }

    if (wireMockEnabled) {
      // clean up stubs, so the server cannot fetch new data when it did not properly cache it
      getWireMockSession.deleteAll()
    }
  }

  def loadJson(): Unit
  def loadRandomData(): Unit
  def getWireMockSession: WireMockSession
}
