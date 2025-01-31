package com.globalrelay.mdscache.performance.config

import com.globalrelay.mdscache.performance.helper.ConfigHelper._

object FeederConfigs {
  val BASE_FEEDER_PATH: String = "feeder"
  
  val USER_PROFILE_FILE_NAME: String = getConfig().getString(getUserProfileJsonFilename)
  val GET_USER_PROFILE_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + USER_PROFILE_FILE_NAME

  val GET_COMPANY_NUMBER_FILE_NAME: String = getConfig().getString(getCompanyNumberJsonFilename)
  val GET_COMPANY_NUMBER_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + GET_COMPANY_NUMBER_FILE_NAME

  val GET_USER_FILE_CAPABILITIES_FILE_NAME: String = getConfig().getString(getUserFileCapabilitiesJsonFilename)
  val GET_USER_FILE_CAPABILITIES_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + GET_USER_FILE_CAPABILITIES_FILE_NAME

  val VIEW_DISCLAIMER_FILE_NAME: String = getConfig().getString(viewDisclaimerJsonFilename)
  val VIEW_DISCLAIMER_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + VIEW_DISCLAIMER_FILE_NAME

  val GET_USER_FILE_NAME: String = getConfig().getString(getUserJsonFilename)
  val GET_USER_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + GET_USER_FILE_NAME

  val RESOLVE_CONTACT_FILE_NAME: String = getConfig().getString(resolveContactJsonFilename)
  val RESOLVE_CONTACT_FEEDER_PATH: String = BASE_FEEDER_PATH + "/" + RESOLVE_CONTACT_FILE_NAME
}
