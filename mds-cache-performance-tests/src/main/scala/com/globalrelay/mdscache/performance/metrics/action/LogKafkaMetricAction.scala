package com.globalrelay.mdscache.performance.metrics.action

import com.globalrelay.mdscache.performance.metrics.{KafkaProcessingMetricsUtil, ResponseBucketHolder}
import io.gatling.commons.stats.{KO, OK, Status}
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.stats.StatsEngine

import java.util.concurrent.ThreadLocalRandom

class LogKafkaMetricAction(val statsEngine: StatsEngine,
                           val metricsUtil: KafkaProcessingMetricsUtil,
                           val scrapeInterval: Int,
                           val next: Action) extends ChainableAction {
  override def name: String = "Kafka Processing Time"
  
  override protected def execute(session: Session): Unit = {
    val responseTimeRecords = metricsUtil.getResponseTimeRecords

    if (!responseTimeRecords.isEmpty) {
      logResponse(session, responseTimeRecords)
    }

    next ! session
  }

  private def logResponse(session: Session, responseTime: ResponseBucketHolder): Unit = {
    responseTime.getResponseBucket.forEach((k, v) => {
      val status: Status = if (k.equals("success")) OK else KO

      var previousLe: Double = 0
      v.forEach((le, count) => {
        var i = 0

        while (i < count) {
          /* Randomly distributes start time for better visualized graphs in Gatling reports. Strictly speaking, this
           random distribution is not accurate, so it won't detect any non-constant rate such as ramp */
          val start = responseTime.getLastFetchTime + ThreadLocalRandom.current().nextLong(
            responseTime.getDelay - scrapeInterval)

          statsEngine.logResponse(
            session.scenario,
            session.groups,
            name,
            start,
            /* Get a random number between 0 and the difference between the previous le and the current le to randomly
            distribute time because it's not possible to get individual records. Keep in mind that it's NOT accurate
            and does NOT reflect the actual distribution */
            start + Math.round(previousLe + ThreadLocalRandom.current().nextDouble(le - previousLe)),
            status,
            Option(null),
            Option(null))

          i += 1
        }
        previousLe = le
      })
    })
  }
}
