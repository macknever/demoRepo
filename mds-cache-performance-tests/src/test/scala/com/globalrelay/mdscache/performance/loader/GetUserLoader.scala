package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.{BASE_FEEDER_PATH, GET_USER_FILE_NAME}
import com.globalrelay.mdscache.performance.feeder.RandomUserFeeder.DATA
import com.globalrelay.mdscache.performance.helper.SimulationHelper.httpClient
import com.globalrelay.mdscache.performance.loader.GetUserLoader.wireMockSession
import com.globalrelay.mdscache.performance.response.ResponseUtils.forGetUser
import com.globalrelay.mdscache.performance.wiremock.WireMock.INSTANCE
import com.globalrelay.mdscache.wiremock.stubs.admin.GetUserStub
import com.globalrelay.nucleus.wiremock.WireMockSession
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}

import java.net.URLEncoder

class GetUserLoader extends AbstractDataLoader {
  override def getWireMockSession: WireMockSession = {
    wireMockSession
  }

  override def loadJson(): Unit = {
    val json = ujson.read(os.read(os.resource / BASE_FEEDER_PATH / GET_USER_FILE_NAME))

    json.arr.foreach { item =>
      val id: String = item("id").str

      send(id)
    }
  }

  override def loadRandomData(): Unit = {
    DATA.foreach { id =>
      send(id)
    }
  }

  private def send(id: String): Unit = {
    if (wireMockEnabled) {
      stubUser(id)
    }

    val encodedId: String = URLEncoder.encode(id, "UTF-8")

    val get: HttpGet = new HttpGet(BASE_URL + "/users/" + encodedId)

    get.addHeader("Authorization", "Bearer " + jwtToken)

    val response: CloseableHttpResponse = httpClient.execute(get)

    if (response.getStatusLine.getStatusCode != 200) {
      throw new IllegalStateException(response.getStatusLine.getStatusCode.toString)
    }

    response.close()
  }

  private def stubUser(id: String): Unit = {
    getWireMockSession.register(new GetUserStub(id).withResponseBody(forGetUser(id)))
  }
}

object GetUserLoader {
  val wireMockSession: WireMockSession = new WireMockSession(INSTANCE, null)
}