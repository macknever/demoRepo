package com.globalrelay.mdscache.performance.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.globalrelay.mdscache.performance.helper.ConfigHelper.*


object WireMock {

  val INSTANCE: WireMock = com.github.tomakehurst.wiremock.client.WireMock.create()
    .scheme(getConfig().getString(wiremockScheme))
    .host(getConfig().getString(wiremockHost))
    .port(getConfig().getInt(wiremockPort))
    .build()
}
