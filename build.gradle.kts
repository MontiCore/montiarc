import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("base")
  id("jacoco-report-aggregation")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")
  alias(libs.plugins.versions)
}

dependencies {
  jacocoAggregation(project(":generators:ma2java"))
  jacocoAggregation(project(":languages:basis"))
  jacocoAggregation(project(":languages:montiarc"))
}

reporting.reports.register("jacocoAggregatedTestReport", JacocoCoverageReport::class) {
  testType.set(TestSuiteType.UNIT_TEST)
}

tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports {
  csv.required.set(true)
  html.required.set(false)
  xml.required.set(true)
  csv.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated.csv"))
  html.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated/html"))
  xml.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated.xml"))
}

tasks.check {
  dependsOn(tasks.named<JacocoReport>("jacocoAggregatedTestReport"))
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
  checkForGradleUpdate = false
  outputFormatter = "json"
  outputDir = "$buildDir/versions"
  reportfileName = "dependency-updates"

  rejectVersionIf {
    isNonStable(candidate.version) && !isNonStable(currentVersion)
  }
}

fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.contains(it) }
  val regex = Regex("^[0-9,.v-]+(-r)?$")
  return !stableKeyword && !regex.matches(version)
}
