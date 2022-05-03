/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("jacoco-report-aggregation")
  id("montiarc.build.modules")
  id("montiarc.build.repositories")
  id("montiarc.build.project-version")
}

dependencies {
  jacocoAggregation(project(":generators:ma2java"))
  jacocoAggregation(project(":languages:basis"))
  jacocoAggregation(project(":languages:montiarc"))
}

reporting.reports.create("jacocoAggregatedTestReport", JacocoCoverageReport::class) {
  testType.set(TestSuiteType.UNIT_TEST)
}

tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.csv.required.set(true)
tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.html.required.set(false)
tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.xml.required.set(false)
tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.csv.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated.csv"))
tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.html.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated/html"))
tasks.getByName<JacocoReport>("jacocoAggregatedTestReport").reports.xml.outputLocation.set(file("$buildDir/reports/test-coverage/jacocoAggregated.xml"))

tasks.check.get().dependsOn(tasks.named<JacocoReport>("jacocoAggregatedTestReport"))


// Adding the following task is necessary in order to publish the plugin.
// It will not be published by running *gradle publish*, because it is only an *includedBuild*
tasks.register("publishPluginToSE-nexus") {
  description = "Publishes the MontiArc gradle plugin to the SE nexus maven repository."
  group = "publishing"
  dependsOn(gradle.includedBuild("plugin").task(":publishJavaPublicationToSE-nexusRepository"))
}

// Adding the following task is necessary in order to publish the plugin.
// It will not be published by running *gradle publish*, because it is only an *includedBuild*
tasks.register("publishPluginToMavenLocal") {
  description = "Publishes the MontiArc gradle plugin to the local maven repository"
  group = "publishing"
  dependsOn(gradle.includedBuild("plugin").task(":publishJavaPublicationToMavenLocal"))
}