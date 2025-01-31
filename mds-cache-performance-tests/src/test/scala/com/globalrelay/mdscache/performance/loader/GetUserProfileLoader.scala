package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.config.FeederConfigs.*
import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.feeder.RandomUserProfileFeeder.DATA
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.helper.SimulationHelper.*
import com.globalrelay.mdscache.performance.loader.GetUserProfileLoader.wireMockSession
import com.globalrelay.mdscache.performance.response.ResponseUtils.forGetUserProfile
import com.globalrelay.mdscache.performance.wiremock.WireMock.INSTANCE
import com.globalrelay.mdscache.wiremock.stubs.mds.GetUserProfileStub
import com.globalrelay.nucleus.wiremock.WireMockSession
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}

class GetUserProfileLoader extends AbstractDataLoader {

  override def getWireMockSession: WireMockSession = {
    wireMockSession
  }

  override def loadJson(): Unit = {
    val json = ujson.read(os.read(os.resource / BASE_FEEDER_PATH / USER_PROFILE_FILE_NAME))

    json.arr.foreach { item =>
      val smId: Int = item("smId").num.toInt

      if (wireMockEnabled) {
        stubUserProfile(smId)
      }

      val get: HttpGet = new HttpGet(BASE_URL + "/user/" + smId + "/profile")
      val response: CloseableHttpResponse = httpClient.execute(get)

      if (response.getStatusLine.getStatusCode != 200) {
        throw new IllegalStateException()
      }

      response.close()
    }
  }

  override def loadRandomData(): Unit = {
    DATA.foreach { smId =>

      if (wireMockEnabled) {
        stubUserProfile(smId)
      }

      val get: HttpGet = new HttpGet(BASE_URL + "/user/" + smId + "/profile")
      val response: CloseableHttpResponse = httpClient.execute(get)

      if (response.getStatusLine.getStatusCode != 200) {
        throw new IllegalStateException()
      }

      response.close()
    }
  }

  private def stubUserProfile(smId: Int): Unit = {
    wireMockSession.register(new GetUserProfileStub(smId).withResponseBody(forGetUserProfile(smId)))
  }
}

object GetUserProfileLoader {
  val wireMockSession: WireMockSession = new WireMockSession(INSTANCE, null)
}