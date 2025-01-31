package com.globalrelay.mdscache.performance.request

import io.gatling.core.session.{Expression, ExpressionSuccessWrapper}

/**
 * Companion object for KafkaAttributes that provides convenient methods to construct a KafkaAttributes instance
 * with default values.
 */
object KafkaAttributes {
  def apply(
             requestName: Expression[String],
             topic: Expression[String],
           ): KafkaAttributes =
    new KafkaAttributes(
      requestName,
      topic,
      None,
      None,
      Set.empty.expressionSuccess,
    )
}

/**
 * KafkaAttributes holds the attributes for generating a Kafka request in a Gatling simulation.
 */
final case class KafkaAttributes(
                                  requestName: Expression[String],
                                  topic: Expression[String],
                                  key: Option[Expression[String]],
                                  payload: Option[Expression[String]],
                                  headers: Expression[Set[Map[String, String]]],
                                )
