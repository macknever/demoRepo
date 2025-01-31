package com.globalrelay.mdscache.performance.helper

object Constant {
  val OPERATION_ID_KEY = "operation_id"
  val API_VERSION_KEY = "api_version"
  val V1 = "v1"
  val V2 = "v2"
  val ID_KEY = "id"
  val SMID_KEY = "smId"
  val COMPANY_ID_KEY = "companyId"
  val USER_ID_KEY = "userId"
  val NUMBER_KEY = "number"
  val ADDRESS_KEY = "address"
  val ADDRESS_TYPE_KEY = "addressType"
  val CONTACT_ID_KEY = "contactId"

  val GET_COMPANY_NUMBER_OPER = "getCompanyNumber"
  val GET_USER_OPER = "getUser"
  val RESOLVE_CONTACT_OPER = "resolveContact"
  val GET_USER_PROFILE_OPER = "getUserProfile"
  val VIEW_DISCLAIMER_OPER = "viewDisclaimer"
  val GET_USER_FILE_CAPABILITIES_OPER = "getUserFileCapabilities"
  
  val MDS_OPERATION_LIST: List[String] = List(GET_COMPANY_NUMBER_OPER, GET_USER_OPER, GET_USER_PROFILE_OPER, 
    VIEW_DISCLAIMER_OPER, GET_USER_FILE_CAPABILITIES_OPER)
  val UCS_OPERATION_LIST: List[String] = List(RESOLVE_CONTACT_OPER)
  
}
