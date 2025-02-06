package com.lawrence.performance.feeder

import io.gatling.core.Predef.*
import play.api.libs.json.*

import scala.io.Source

object RandomAuthorFeeder {
  def getData(fileName: String): Array[Map[String, String]] = {
    val bufferedSource = Source.fromFile(fileName)
    val data = bufferedSource.getLines().map { line =>
      val cols = line.split("\t")
      val id = cols(1)
      val jsonPayload = cols(4)
      val (name, personalName) = parseAuthor(jsonPayload)

      Map("id" -> id, "name" -> name, "personalName" -> personalName, "topic" -> "notifications")
    }.toArray

    bufferedSource.close()
    data
  }

  def parseAuthor(jsonString: String): (String, String) = {

    // Parse the JSON string
    val json = Json.parse(jsonString)

    // Extract the "name" field
    val name: String = (json \ "name").as[String]

    // Try to extract "personalName"; if not found, fall back to "personal_name"
    val personalName: String =
        (json \ "personal_name").as[String]

    (name, personalName)
  }


}
