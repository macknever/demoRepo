package com.globalrelay.mdscache.performance.action

import com.globalrelay.mdscache.performance.protocol.KafkaProtocol
import com.globalrelay.mdscache.performance.request.KafkaAttributes
import io.gatling.commons.stats.{KO, OK}
import io.gatling.commons.validation.{SuccessWrapper, Validation}
import io.gatling.core.action.RequestAction
import io.gatling.core.session.{Expression, Session, resolveOptionalExpression}
import io.gatling.core.stats.StatsEngine
import io.gatling.core.util.NameGen
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.header.Headers

/**
 * Utility class to encapsulate logic that runs before and after executing a main action.
 * This is typically used to define "around" behavior for Kafka actions.
 *
 * @param before Function to be run before the main action.
 * @param after  Function to be run after the main action.
 */
class Around(before: () => Unit, after: () => Unit) {
  /**
   * Executes the provided function, with the specified 'before' and 'after' logic wrapped around it.
   *
   * @param f The main action to execute.
   */
  def apply(f: => Any): Unit = {
    before()
    f
    after()
  }
}

/**
 * Base class representing a Kafka action that sends messages to Kafka and logs the result.
 *
 * This class defines the core functionality for sending Kafka messages within a Gatling simulation,
 * including resolving Kafka request attributes.
 * It also integrates with Gatling's stats engine to log the information of sending action
 */
abstract class KafkaAction(
                            attributes: KafkaAttributes,
                            protocol: KafkaProtocol,
                            producer: KafkaProducer[String, String],
                            val statsEngine: StatsEngine
                          ) extends RequestAction with NameGen {

  override val requestName: Expression[String] = attributes.requestName

  /**
   * Sends a Kafka request using the provided Gatling [[Session]].
   * This method resolves the necessary attributes for the Kafka message, creates a ProducerRecord,
   * and sends it via the Kafka producer.
   *
   * @param session The Gatling session containing user-specific data and context.
   * @return Validation indicating the success or failure of the request sending process.
   */
  override def sendRequest(session: Session): Validation[Unit] = {
    for {
      reqName <- requestName(session)
      topic <- attributes.topic(session)
      key <- resolveOptionalExpression(attributes.key, session)
      payload <- resolveOptionalExpression(attributes.payload, session)
      headers <- resolveHeaders(attributes.headers, session)
      record <- new ProducerRecord(topic, null, key.getOrElse(""), payload.getOrElse(""), headers).success
      around <- aroundSend(reqName, session, record, topic)
    } yield {
      val start = clock.nowMillis
      val outcome: Unit = around(producer.send(record))

      outcome.success.onFailure(fail => {
        val end = clock.nowMillis
        statsEngine.logResponse(session.scenario, session.groups, reqName, start, end, KO, None, Option(fail))
      }
      ).success.onSuccess(successWrapper => {
        val end = clock.nowMillis
        statsEngine.logResponse(session.scenario, session.groups, reqName, start, end, OK, None, Option(successWrapper.value.toString))
      }
      )
    }
  }

  /**
   * Resolves the Kafka message headers from headers in kafka attributes
   *
   * @param value   The expression representing the headers as a set of maps.
   * @param session The Gatling session used to evaluate the expression.
   * @return Validation containing the resolved headers or an error message if resolution fails.
   */
  protected def resolveHeaders(value: Expression[Set[Map[String, String]]], session: Session): Validation[Headers]

  /**
   * Abstract method for defining the before and after section around sending the Kafka request.
   */
  protected def aroundSend(requestName: String, session: Session, producerRecord: ProducerRecord[String, String], topic: String): Validation[Around]
}
