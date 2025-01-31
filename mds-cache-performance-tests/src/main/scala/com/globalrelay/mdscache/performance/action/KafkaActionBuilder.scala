package com.globalrelay.mdscache.performance.action

import com.globalrelay.mdscache.performance.protocol.{KafkaComponents, KafkaProtocol}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.protocol.ProtocolComponentsRegistry

/**
 * Abstract builder for Kafka actions in Gatling.
 */
abstract class KafkaActionBuilder extends ActionBuilder {

  /**
   * Retrieves the Kafka components from the protocol registry.
   *
   * @param protocolComponentsRegistry The registry of protocol components.
   * @return The Kafka components associated with the protocol.
   */
  protected def components(protocolComponentsRegistry: ProtocolComponentsRegistry): KafkaComponents = {
    protocolComponentsRegistry.components(KafkaProtocol.kafkaProtocolKey)
  }

}
