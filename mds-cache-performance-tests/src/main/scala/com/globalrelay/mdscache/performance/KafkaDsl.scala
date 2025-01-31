package com.globalrelay.mdscache.performance

import com.globalrelay.mdscache.performance.protocol.{KafkaProtocol, KafkaProtocolBuilder}
import com.globalrelay.mdscache.performance.request.{KafkaDslBuilderBase, SendDslBuilder}
import io.gatling.core.Predef.*
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Expression

import scala.language.implicitConversions
import scala.util.Success

/**
 * KafkaDsl trait provides the core DSL methods for defining and configuring Kafka-based simulations in Gatling.
 *
 * It allows for setting up Kafka protocol and building actions that interact with Kafka
 * The DSL methods are designed to be used fluently within Gatling simulation scenarios.
 */
trait KafkaDsl {

  /**
   * Creates a default KafkaProtocolBuilder.
   *
   * @param configuration The GatlingConfiguration that contains user-defined settings.
   * @return A default KafkaProtocolBuilder instance for configuring Kafka protocol settings.
   */
  def kafka(implicit configuration: GatlingConfiguration): KafkaProtocolBuilder = KafkaProtocolBuilder.Default

  /**
   * Creates a KafkaDslBuilderBase for building a Kafka action. This would be the start of fluent builder
   *
   * @param requestName Name of the Kafka request.
   * @return A KafkaDslBuilderBase to configure the Kafka request.
   */
  def kafka(requestName: Expression[String]): KafkaDslBuilderBase = new KafkaDslBuilderBase(requestName)

  /**
   * Implicit conversion from KafkaProtocolBuilder to KafkaProtocol, simplifying usage in DSL.
   */
  implicit def kafkaProtocolBuilder2KafkaProtocol(builder: KafkaProtocolBuilder): KafkaProtocol = builder.build

  /**
   * Implicit conversion from SendDslBuilder to ActionBuilder, allowing seamless integration into Gatling scenarios.
   */
  implicit def kafkaDslBuilder2ActionBuilder(builder: SendDslBuilder): ActionBuilder = builder.build

}
