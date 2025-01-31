package com.globalrelay.mdscache.performance.helper

import com.typesafe.config.Config
import org.apache.http.conn.ssl.{NoopHostnameVerifier, TrustSelfSignedStrategy}
import org.apache.http.impl.client.{CloseableHttpClient, HttpClients}
import org.apache.http.ssl.SSLContexts
import org.json4s.*

import javax.net.ssl.SSLContext

/**
 * Helper object to manage key simulations.
 */
object SimulationHelper {

  implicit val formats: DefaultFormats.type = DefaultFormats
  val config: Config = ConfigHelper.getConfig()
  val baseUrl: String = config.getString(ConfigHelper.baseUrl)
  val UC_MDS_CACHE_AUD: String = "uc-mds-cache"
  val OPP_CLAIM_KEY: String = "opp"

  val httpClient: CloseableHttpClient = getHttpClient


  /**
   * Returns a secure HTTP client.
   */
  private def getHttpClient: CloseableHttpClient = {
    val sslContext: SSLContext = SSLContexts.custom().loadTrustMaterial(new TrustSelfSignedStrategy()).build()

    HttpClients.custom()
      .setSSLContext(sslContext)
      .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
      .build()
  }
}


