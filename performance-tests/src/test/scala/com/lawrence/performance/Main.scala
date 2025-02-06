package com.lawrence.performance

import com.lawrence.performance.helper.{ConfigHelper, PathHelper}
import com.typesafe.config.Config
import io.gatling.app.Gatling


/**
 * Used to run the tests from the IDE.
 *
 * All performance tests can be run at once with the maven perf profile.
 * e.g. mvn clean install -P performance-test-all
 * Results are located in target/gatling
 */
object Main extends App {
  val config: Config = ConfigHelper.getConfig()

  Gatling.main(Array(
    "-s", "com.lawrence.performance.simulations.PostAuthor",
    "-rf", PathHelper.resultsDirectory.toString,
    "-rd", config.getString(ConfigHelper.runDescription)))
}
