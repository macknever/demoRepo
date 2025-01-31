package com.globalrelay.mdscache.performance.protocol

import org.apache.kafka.clients.producer.ProducerConfig.*

import scala.language.implicitConversions

/**
 * The builder class responsible for configuring and building KafkaProtocol instances.
 *
 * It provides methods to configure essential Kafka producer properties.
 *
 */
final case class KafkaProtocolBuilder(kafkaProtocol: KafkaProtocol) {

  def broker(brokers: String): KafkaProtocolBuilder = {
    addProducerProperty(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
  }

  def securityProtocol(protocol: String, truststoreLocation: String, password: String): KafkaProtocolBuilder = {
    addProducerProperty("security.protocol",protocol)
      .addProducerProperty("ssl.truststore.location", truststoreLocation)
      .addProducerProperty("ssl.truststore.password", password)
  }

  def producerKeySerializer(serializer: String): KafkaProtocolBuilder =
    addProducerProperty(KEY_SERIALIZER_CLASS_CONFIG, serializer)

  def producerValueSerializer(serializer: String): KafkaProtocolBuilder =
    addProducerProperty(VALUE_SERIALIZER_CLASS_CONFIG, serializer)

  /**
   * Adds a property to the Kafka producer configuration.
   *
   * @param key   The configuration property key (e.g., serializer settings or broker settings).
   * @param value The value for the configuration key.
   * @return A KafkaProtocolBuilder instance with the added property.
   */
  private def addProducerProperty(key: String, value: String): KafkaProtocolBuilder = {
    val updatedProperties = kafkaProtocol.producerProperties + (key -> value)
    this.copy(kafkaProtocol = kafkaProtocol.copy(producerProperties = updatedProperties))
  }

  def build: KafkaProtocol = kafkaProtocol
}

/**
 * Companion object for KafkaProtocolBuilder that provides a default instance and implicit conversions.
 */
object KafkaProtocolBuilder {
  implicit def toKafkaProtocol(builder: KafkaProtocolBuilder): KafkaProtocol = builder.build

  val Default: KafkaProtocolBuilder = KafkaProtocolBuilder(KafkaProtocol.apply())
}
