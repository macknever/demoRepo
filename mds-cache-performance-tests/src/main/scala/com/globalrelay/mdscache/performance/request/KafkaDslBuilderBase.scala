package com.globalrelay.mdscache.performance.request

import com.globalrelay.mdscache.performance.action.KafkaSendBuilder
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.Expression

import scala.language.{existentials, implicitConversions}

final class KafkaDslBuilderBase(requestName: Expression[String]) {
  def send: SendDslBuilder.Topic = SendDslBuilder.Topic(requestName)
}

object SendDslBuilder {
  final case class Topic(requestName: Expression[String]) {
    def topic(topicName: Expression[String]) = SendDslBuilder(
      KafkaAttributes(requestName, topicName),
      new KafkaSendBuilder(_))
  }
}

final case class SendDslBuilder(attributes: KafkaAttributes, factory: KafkaAttributes => ActionBuilder) {

  def key(key: Expression[String]): SendDslBuilder = {
    this.copy(attributes = attributes.copy(key = Some(key)))
  }

  def payload(payload: Expression[String]): SendDslBuilder = {
    this.copy(attributes = attributes.copy(payload = Some(payload)))
  }

  def headers(newHeaders: Expression[Set[Map[String, String]]]): SendDslBuilder = {
    val updatedHeaders: Expression[Set[Map[String, String]]] = session => {
      for {
        existingHeaders <- attributes.headers(session)
        newHeadersMap <- newHeaders(session)
      } yield {
        existingHeaders ++ newHeadersMap
      }
    }

    this.copy(attributes = attributes.copy(headers = updatedHeaders))
  }
  
  def build: ActionBuilder = factory(attributes)
}

