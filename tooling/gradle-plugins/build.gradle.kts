/* (c) https://github.com/MontiCore/monticore */
group = "montiarc.tooling"

tasks.register("publishAll") {
  dependsOn(subprojects.map {it.tasks.named("publish") })
}
