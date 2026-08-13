/* (c) https://github.com/MontiCore/monticore */
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("base")
  id("jacoco-report-aggregation")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")
  alias(libs.plugins.versions)
}

dependencies {
  jacocoAggregation(project(":generators:ma2jsim"))
  jacocoAggregation(project(":languages:basis"))
  jacocoAggregation(project(":languages:montiarc"))
}

reporting.reports.register("jacocoAggregatedTestReport", JacocoCoverageReport::class) {
  testSuiteName.set("test")
}

tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports {
  csv.required.set(true)
  html.required.set(false)
  xml.required.set(true)
  csv.outputLocation.set(layout.buildDirectory.file("reports/test-coverage/jacocoAggregated.csv"))
  html.outputLocation.set(layout.buildDirectory.dir("reports/test-coverage/jacocoAggregated/html"))
  xml.outputLocation.set(layout.buildDirectory.file("reports/test-coverage/jacocoAggregated.xml"))
}

tasks.check {
  dependsOn(tasks.named<JacocoReport>("jacocoAggregatedTestReport"))
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates").configure {
  checkForGradleUpdate = false
  outputFormatter = "json"
  outputDir = layout.buildDirectory.dir("versions").get().asFile.absolutePath
  reportfileName = "dependency-updates"

  rejectVersionIf {
    (isNonStable(candidate.version) && !isNonStable(currentVersion))
        || (candidate.group == "com.jetbrains.intellij.java"
        && candidate.module == "java-compiler-ant-tasks")
  }
}

private fun isNonStable(version: String): Boolean {
  val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.contains(it) }
  val regex = Regex("^[0-9,.v-]+(-r)?$")
  return !stableKeyword && !regex.matches(version)
}
