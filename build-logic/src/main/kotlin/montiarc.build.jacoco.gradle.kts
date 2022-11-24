/* (c) https://github.com/MontiCore/monticore */

plugins {
  java
  jacoco
}

tasks.jacocoTestReport {
  reports {
    csv.required.set(true)
    html.required.set(false)
    xml.required.set(true)
    csv.outputLocation.set(reporting.baseDirectory.file("test-coverage/jacoco.csv"))
    html.outputLocation.set(reporting.baseDirectory.dir("test-coverage/jacoco/html"))
    xml.outputLocation.set(reporting.baseDirectory.file("test-coverage/jacoco.xml"))
  }
}

tasks.test { finalizedBy(tasks.jacocoTestReport) }
tasks.jacocoTestReport { dependsOn(tasks.test) }