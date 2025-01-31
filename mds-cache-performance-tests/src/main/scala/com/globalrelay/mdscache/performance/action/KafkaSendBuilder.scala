package com.globalrelay.mdscache.performance.action

import com.globalrelay.mdscache.performance.protocol.KafkaComponents
import com.globalrelay.mdscache.performance.request.KafkaAttributes
import io.gatling.core.action.Action
import io.gatling.core.structure.ScenarioContext

/**
 * Builder for the Send action that sends messages to Kafka in a Gatling simulation.
 *
 * @param attributes Kafka request attributes.
 */
final class KafkaSendBuilder(attributes: KafkaAttributes) extends KafkaActionBuilder {

  override def build(ctx: ScenarioContext, next: Action): Action = {

    val kafkaComponents: KafkaComponents = components(ctx.protocolComponentsRegistry)

    new KafkaSend(
      attributes,
      kafkaComponents.kafkaProtocol,
      kafkaComponents.kafkaProducer,
      ctx.coreComponents.statsEngine,
      ctx.coreComponents.clock,
      next
    )
  }
}
