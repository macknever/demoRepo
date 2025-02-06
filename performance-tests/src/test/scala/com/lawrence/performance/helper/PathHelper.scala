package com.lawrence.performance.helper

import java.nio.file.Path

/**
 * Intended for Main class used to run tests from IDE.
 */
object PathHelper {
  val gatlingConfUrl: Path = Path.of(getClass.getClassLoader.getResource("gatling.conf").toURI)
  val projectRootDir: Path = gatlingConfUrl.getParent.getParent.getParent

  val mavenSourcesDirectory: Path = projectRootDir.resolve("/src/test/scala")
  val mavenResourcesDirectory: Path = projectRootDir.resolve("/src/test/resources")
  val mavenTargetDirectory: Path = projectRootDir.resolve("/target")
  val mavenBinariesDirectory: Path = mavenTargetDirectory.resolve("/test-classes")

  val resourcesDirectory: Path = mavenResourcesDirectory
  val resultsDirectory: Path = mavenTargetDirectory.resolve("/gatling")
}
