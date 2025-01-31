package com.globalrelay.mdscache.performance.protocol

import io.gatling.core.CoreComponents
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.protocol.{Protocol, ProtocolKey}
import org.apache.kafka.clients.producer.KafkaProducer

import scala.jdk.CollectionConverters.*

/**
 * Represents the Kafka protocol configuration for a Gatling simulation.
 */
object KafkaProtocol {

  val kafkaProtocolKey: ProtocolKey[KafkaProtocol, KafkaComponents] = new ProtocolKey[KafkaProtocol, KafkaComponents] {
    override def protocolClass: Class[Protocol] = classOf[KafkaProtocol].asInstanceOf[Class[Protocol]]

    override def defaultProtocolValue(configuration: GatlingConfiguration): KafkaProtocol =
      throw new IllegalStateException("Can't provide a default value for KafkaProtocol")

    override def newComponents(coreComponents: CoreComponents): KafkaProtocol => KafkaComponents = {
      kafkaProtocol => {
        val producerProperties = kafkaProtocol.producerProperties.asJava

        val producer = new KafkaProducer[String, String](producerProperties)

        coreComponents.actorSystem.registerOnTermination {
          producer.close()
        }

        KafkaComponents(kafkaProtocol, producer)
      }
    }
  }

  def apply(): KafkaProtocol = KafkaProtocol(
    producerProperties = Map.empty,
  )
}

final case class KafkaProtocol(producerProperties: Map[String, AnyRef]) extends Protocol {
  type Components = KafkaComponents
}
