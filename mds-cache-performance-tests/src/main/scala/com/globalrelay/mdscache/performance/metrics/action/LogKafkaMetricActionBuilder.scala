package com.globalrelay.mdscache.performance.metrics.action

import com.globalrelay.mdscache.performance.metrics.KafkaProcessingMetricsUtil
import io.gatling.core.action.Action
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.structure.ScenarioContext

/**
 * The ActionBuilder for logging metric. This class is required for actions to be run in gatling via exec().
 */
class LogKafkaMetricActionBuilder(val metricsUtil: KafkaProcessingMetricsUtil, val scrapeInterval: Int) 
  extends ActionBuilder {

  /**
   * We must implement build() for custom action builders. For the kafka protocol, we do not require
   * any additional functionality in build(), so we import the components from default gatling http.
   * The statsEngine is responsible for keeping track of success/fail and response times.
   *
   * @param ctx  the current gatling scenario context
   * @param next the next action in the chain
   * @return
   */
  override def build(ctx: ScenarioContext, next: Action): Action = {
    val statsEngine = ctx.coreComponents.statsEngine
    new LogKafkaMetricAction(statsEngine, metricsUtil, scrapeInterval, next)
  }
}