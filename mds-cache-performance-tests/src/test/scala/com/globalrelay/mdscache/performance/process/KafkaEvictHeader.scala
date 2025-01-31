package com.globalrelay.mdscache.performance.process

import com.globalrelay.mdscache.performance.Predef.*
import com.globalrelay.mdscache.performance.helper.ConfigHelper
import com.typesafe.config.Config
import io.gatling.core.Predef.*
import io.gatling.core.structure.ChainBuilder

object KafkaEvictHeader {
  val config: Config = ConfigHelper.getConfig()
  val mdsTopic: String = config.getString(ConfigHelper.kafkaMdsTopic)
  val ucsTopic: String = config.getString(ConfigHelper.kafkaUcsTopic)

  def sendMixedMdsEvictHeader: ChainBuilder = {
    exec {
      kafka("Send evict header")
        .send
        .topic(mdsTopic)
        .payload(randomPayload(200, 300))
        .headers(session => {
          session("evict").as[Set[Map[String, String]]]
        }
        )
    }
  }

  def sendMixedUcsEvictHeader: ChainBuilder = {
    exec {
      kafka("Send evict header")
        .send
        .topic(ucsTopic)
        .payload(randomPayload(200, 300))
        .headers(session => {
          session("evict").as[Set[Map[String, String]]]
        }
        )
    }
  }

  implicit class ExtendedRandom(ran: scala.util.Random) {
    /**
     * Generates a random byte array of the specified size.
     *
     * @param size The size of the byte array to generate. Must be a non-negative integer.
     * @return An `Array[Byte]` containing random bytes.
     * @throws NegativeArraySizeException if size is negative.
     */
    def nextByteArray(size: Int): Array[Byte] = {
      val arr = new Array[Byte](size)
      ran.nextBytes(arr)
      arr
    }
  }

  /**
   * Generate a random-sized byte array within a range and convert it to a String
   */
  def randomPayload(minSize: Int, maxSize: Int): String = {
    implicit val random: scala.util.Random = new scala.util.Random
    require(minSize > 0 && maxSize >= minSize, "Invalid range for byte array size")

    val size = random.between(minSize, maxSize + 1)
    val byteArray = random.nextByteArray(size)
    new String(byteArray, "UTF-8")
  }

}
