package com.globalrelay.mdscache.performance.action

import com.fasterxml.jackson.databind.ObjectMapper
import com.globalrelay.mdscache.performance.protocol.KafkaProtocol
import com.globalrelay.mdscache.performance.request.KafkaAttributes
import io.gatling.commons.util.Clock
import io.gatling.commons.validation.{FailureWrapper, SuccessWrapper, Validation}
import io.gatling.core.action.Action
import io.gatling.core.session.{Expression, Session}
import io.gatling.core.stats.StatsEngine
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.header.internals.RecordHeaders

import scala.jdk.CollectionConverters.*

/**
 * A concrete implementation of KafkaAction that sends a message to Kafka.
 *
 * @param attributes  Kafka request attributes.
 * @param protocol    Kafka protocol configuration.
 * @param producer    Kafka producer used to send messages.
 * @param statsEngine Stats engine to log the request statistics.
 * @param clock       Clock for timing the request.
 * @param next        The next action to execute in the scenario.
 */
class KafkaSend(
            attributes: KafkaAttributes,
            protocol: KafkaProtocol,
            producer: KafkaProducer[String, String],
            statsEngine: StatsEngine,
            val clock: Clock,
            val next: Action,
          ) extends KafkaAction(attributes, protocol, producer, statsEngine) {
  override val name: String = genName("kafkaSend")

  /**
   * Resolves the headers from the given session and scala [[Set]] of [[Map]] to [[RecordHeaders]]
   *
   * @param evictSet a set of map containing evict info
   * @param session  Gatling session used to resolve the expression.
   * @return Validation containing the resolved headers or an error.
   */
  override protected def resolveHeaders(evictSet: Expression[Set[Map[String, String]]], session: Session): Validation[Headers] = {
    val headers = new RecordHeaders()
    val objectMapper = new ObjectMapper()

    evictSet(session).flatMap { resolvedEvictSet =>
      try {
        // Convert the Set[Map[String, String]] to a Java-compatible format
        // otherwise the key and value will contain scala class info
        val javaCompatibleSet = resolvedEvictSet.map(_.asJava).asJava

        val evictValueBytes = objectMapper.writeValueAsBytes(javaCompatibleSet)
        headers.add("evict", evictValueBytes)
        headers.success
      } catch {
        case e: Exception =>
          s"Failed to serialize evict set: ${e.getMessage}".failure
      }
    }
  }

  override protected def aroundSend(requestName: String, session: Session, producerRecord: ProducerRecord[String, String], topic: String): Validation[Around] = {
    new Around(
      before = () => (),
      after = () => {
        next ! session // Trigger the next action in the scenario after sending
      }
    ).success
  }
}
