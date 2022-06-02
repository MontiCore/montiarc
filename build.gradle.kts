/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("jacoco-report-aggregation")
  id("montiarc.build.modules")
  id("montiarc.build.repositories")
}

dependencies {
  jacocoAggregation(project(":generator:ma2java"))
  jacocoAggregation(project(":language:basis"))
  jacocoAggregation(project(":language:montiarc"))
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
