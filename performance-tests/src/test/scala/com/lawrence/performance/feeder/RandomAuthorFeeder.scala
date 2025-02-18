package com.lawrence.performance.feeder

import io.gatling.core.Predef.*
import play.api.libs.json.*

import scala.io.{Codec, Source}

object RandomAuthorFeeder {
  implicit val codec: Codec = Codec.UTF8
  val ALLOWED_TOPIC: String = "cp.msg-mds.local.ca.nvan.directory.changelog"

  def getData(fileName: String): Array[Map[String, String]] = {
    val bufferedSource = Source.fromResource(fileName)
    val data = bufferedSource.getLines().map { line =>
      val cols = line.split("\t")
      val id = cols(1)
      val jsonPayload = cols(4)
      val (name, personalName) = parseAuthor(jsonPayload)

      Map("id" -> id, "name" -> name, "personalName" -> personalName, "topic" -> ALLOWED_TOPIC)
    }.toArray

    bufferedSource.close()
    data
  }

  def parseAuthor(jsonString: String): (String, String) = {

    // Parse the JSON string
    val json = Json.parse(jsonString)

    // Extract the "name" field
    val name: String = (json \ "name").asOpt[String].getOrElse("NO_NAME")

    // Try to extract "personalName"; if not found, fall back to "personal_name"
    val personalName: String =
        (json \ "personal_name").asOpt[String].getOrElse("NO_PERSONAL_NAME")

    (name, personalName)
  }


}
