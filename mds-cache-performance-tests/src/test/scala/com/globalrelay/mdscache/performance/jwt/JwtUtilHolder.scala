package com.globalrelay.mdscache.performance.jwt

import com.globalrelay.mdscache.guice.modules.JwtUtilModule
import com.globalrelay.mdscache.performance.config.PropertiesInjector
import com.globalrelay.mdscache.util.JwtUtil
import com.google.inject.Injector

object JwtUtilHolder {
  val injector: Injector = PropertiesInjector.getInstance().createChildInjector(new JwtUtilModule)
  
  val jwtUtil: JwtUtil = injector.getInstance(classOf[JwtUtil])
}
