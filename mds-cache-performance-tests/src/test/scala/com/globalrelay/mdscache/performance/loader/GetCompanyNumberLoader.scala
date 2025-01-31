package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.{BASE_FEEDER_PATH, GET_COMPANY_NUMBER_FILE_NAME}
import com.globalrelay.mdscache.performance.feeder.RandomCompanyNumberFeeder.DATA
import com.globalrelay.mdscache.performance.helper.SimulationHelper.httpClient
import com.globalrelay.mdscache.performance.loader.GetCompanyNumberLoader.wireMockSession
import com.globalrelay.mdscache.performance.response.ResponseUtils.forGetCompanyNumber
import com.globalrelay.mdscache.performance.wiremock.WireMock.INSTANCE
import com.globalrelay.mdscache.wiremock.stubs.admin.GetCompanyNumberStub
import com.globalrelay.nucleus.wiremock.WireMockSession
import org.apache.http.client.methods.{CloseableHttpResponse, HttpGet}

import java.net.URLEncoder

class GetCompanyNumberLoader extends AbstractDataLoader {

  override def getWireMockSession: WireMockSession = {
    wireMockSession
  }

  override def loadJson(): Unit = {
    val json = ujson.read(os.read(os.resource / BASE_FEEDER_PATH / GET_COMPANY_NUMBER_FILE_NAME))

    json.arr.foreach { item =>
      val id: Int = item("id").num.toInt
      val number: String = item("number").str

      send(id, number)
    }
  }

  override def loadRandomData(): Unit = {
    DATA.foreach { map =>
      val id: Int = map("id").asInstanceOf[Int]
      val number: String = map("number").asInstanceOf[String]

      send(id, number)
    }
  }

  private def send(id: Int, number: String): Unit = {
    if (wireMockEnabled) {
      stubCompanyNumber(id, number)
    }

    val encodedNumber: String = URLEncoder.encode(number, "UTF-8")

    val get: HttpGet = new HttpGet(BASE_URL + "/company/" + id + "/number/" + encodedNumber)

    get.addHeader("Authorization", "Bearer " + jwtToken)

    val response: CloseableHttpResponse = httpClient.execute(get)

    if (response.getStatusLine.getStatusCode != 200) {
      throw new IllegalStateException(response.getStatusLine.getStatusCode.toString)
    }

    response.close()
  }

  private def stubCompanyNumber(id: Int, number: String): Unit = {
    getWireMockSession.register(new GetCompanyNumberStub(id, number).withResponseBody(forGetCompanyNumber(id, number)))
  }
}

object GetCompanyNumberLoader {
  val wireMockSession: WireMockSession = new WireMockSession(INSTANCE, null)
}