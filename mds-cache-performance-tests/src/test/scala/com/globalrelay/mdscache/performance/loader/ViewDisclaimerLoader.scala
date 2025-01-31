package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs._
import com.globalrelay.mdscache.performance.feeder.RandomDisclaimerFeeder.DATA
import com.globalrelay.mdscache.performance.helper.SimulationHelper.httpClient
import com.globalrelay.mdscache.performance.loader.ViewDisclaimerLoader.wireMockSession
import com.globalrelay.mdscache.performance.response.ResponseUtils.forViewDisclaimer
import com.globalrelay.mdscache.performance.wiremock.WireMock.INSTANCE
import com.globalrelay.mdscache.wiremock.stubs.admin.ViewDisclaimerStub
import com.globalrelay.nucleus.wiremock.WireMockSession
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}

class ViewDisclaimerLoader extends AbstractDataLoader {
  override def getWireMockSession: WireMockSession = {
    wireMockSession
  }

  override def loadJson(): Unit = {
    val json = ujson.read(os.read(os.resource / BASE_FEEDER_PATH / VIEW_DISCLAIMER_FILE_NAME))

    json.arr.foreach { item =>
      val id: Int = item("id").num.toInt

      if (wireMockEnabled) {
        stubDisclaimer(id)
      }

      val get: HttpGet = new HttpGet(BASE_URL + "/company/" + id + "/disclaimer")
      get.addHeader("Authorization", "Bearer " + jwtToken)

      val response: CloseableHttpResponse = httpClient.execute(get)

      if (response.getStatusLine.getStatusCode != 200) {
        throw new IllegalStateException()
      }

      response.close()
    }
  }

  override def loadRandomData(): Unit = {
    DATA.foreach { id =>

      if (wireMockEnabled) {
        stubDisclaimer(id)
      }

      val get: HttpGet = new HttpGet(BASE_URL + "/company/" + id + "/disclaimer")
      get.addHeader("Authorization", "Bearer " + jwtToken)

      val response: CloseableHttpResponse = httpClient.execute(get)

      if (response.getStatusLine.getStatusCode != 200) {
        throw new IllegalStateException()
      }

      response.close()
    }
  }

  private def stubDisclaimer(id: Int): Unit = {
    wireMockSession.register(new ViewDisclaimerStub(id).withResponseBody(forViewDisclaimer()))
  }
}

object ViewDisclaimerLoader {
  val wireMockSession: WireMockSession = new WireMockSession(INSTANCE, null)
}