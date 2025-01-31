package com.globalrelay.mdscache.performance.loader

import com.globalrelay.mdscache.performance.config.ApiConfigs.BASE_URL
import com.globalrelay.mdscache.performance.config.FeederConfigs.{BASE_FEEDER_PATH, RESOLVE_CONTACT_FILE_NAME}
import com.globalrelay.mdscache.performance.feeder.RandomContactFeeder.DATA
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.globalrelay.mdscache.performance.helper.ConfigHelper.getConfig
import com.globalrelay.mdscache.performance.helper.SimulationHelper.httpClient
import com.globalrelay.mdscache.performance.jwt.JwtUtilHolder
import com.globalrelay.mdscache.performance.loader.ResolveContactLoader.wireMockSession
import com.globalrelay.mdscache.performance.response.ResponseUtils.forResolveContact
import com.globalrelay.mdscache.performance.wiremock.WireMock.INSTANCE
import com.globalrelay.mdscache.wiremock.stubs.contact.ResolveContactStub
import com.globalrelay.nucleus.wiremock.WireMockSession
import org.apache.http.client.methods.{CloseableHttpResponse, HttpPost}
import org.apache.http.entity.StringEntity

import java.net.URLEncoder

class ResolveContactLoader extends AbstractDataLoader {
  override def getWireMockSession: WireMockSession = {
    wireMockSession
  }
  
  override def loadJson(): Unit = {
    val json = ujson.read(os.read(os.resource / BASE_FEEDER_PATH / RESOLVE_CONTACT_FILE_NAME))

    json.arr.foreach { item =>
      val id: String = item("id").asInstanceOf[String]
      val address: String = item("address").asInstanceOf[String]
      val addressType: String = item("addressType").asInstanceOf[String]
      val contactId: String = item("contactId").asInstanceOf[String]

      send(id, address, addressType, contactId)
    }
  }

  override def loadRandomData(): Unit = {
    DATA.foreach { map =>
      val id: String = map("id").asInstanceOf[String]
      val address: String = map("address").asInstanceOf[String]
      val addressType: String = map("addressType").asInstanceOf[String]
      val contactId: String = map("contactId").asInstanceOf[String]

      send(id, address, addressType, contactId)
    }
  }

  private def send(id: String, address: String, addressType: String, contactId: String): Unit = {
    if (wireMockEnabled) {
      stubContact(id)
    }

    val encodedId: String = URLEncoder.encode(id, "UTF-8")

    val post: HttpPost = new HttpPost(BASE_URL + "/users/" + encodedId + "/contacts/resolve")

    val body: String = s"""{ "address": "$address", "addressType": "$addressType", "contactId": "$contactId" }"""

    post.setEntity(new StringEntity(body))
    post.addHeader("Content-type", "application/json");
    post.addHeader("Authorization", "Bearer " + jwtToken)

    val response: CloseableHttpResponse = httpClient.execute(post)

    if (response.getStatusLine.getStatusCode != 200) {
      throw new IllegalStateException(response.getStatusLine.getStatusCode.toString)
    }

    response.close()
  }

  private def stubContact(id: String): Unit = {
    wireMockSession.register(new ResolveContactStub(id)
      .withResponseBody(forResolveContact()))
  }
}

object ResolveContactLoader {
  val wireMockSession: WireMockSession = new WireMockSession(INSTANCE, null)
}
