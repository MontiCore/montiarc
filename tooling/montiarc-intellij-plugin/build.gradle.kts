/* (c) https://github.com/MontiCore/monticore */

plugins {
  id("montiarc.build.intellij-plugin")
}

val lspJar = configurations.register("languageServerJar") {
  isCanBeResolved = true
  isCanBeConsumed = false
}

dependencies {
  add(
    lspJar.name,
    project(
      path = ":tooling:language-server",
      configuration = "languageServerJar"
    )
  )
}

sourceSets {
  main {
    java {
      srcDir("build/generated-sources/MontiArcWithCD4A/plugins/montiarcwithcd4a-intellij-plugin/src/main/java")
    }
    resources {
      srcDir("build/generated-sources/MontiArcWithCD4A/plugins/montiarcwithcd4a-intellij-plugin/src/main/resources")
    }
  }
}

tasks.configureEach {
  enabled = enabled && project.hasProperty("enableLanguageServer")
}

java {
  withSourcesJar()
}

// create needs to be used instead of register, since register is evaluated lazily and this too late,
// since this task creates other tasks
val autoconfigure = tasks.create<de.mclsg.task.AutoconfigureTask>("autoconfigure") {
  configure<de.mclsg.MCLSGPluginAggregationExtension> {
    setLanguageAggregationName("MontiArcWithCD4A")
    setTargetPackage("montiarc_with_cd4a")
    setHandCodedDirBase("${projectDir}/main")

    member("MontiArc", "arc", true)
    member("de.monticore.CD4Analysis", "cd", false)
    languageServerJar(
      File(lspJar.get().asPath)
    )
  }
  including(
    de.mclsg.TaskTypes.INTELLIJ_PLUGIN,
  )
  excluding(
    de.mclsg.TaskTypes.BUILD_INTELLIJ_PLUGIN,
    de.mclsg.TaskTypes.LANGUAGE_SERVER
  )
  autoconfigureLspTasks()
}

tasks.named("build") {
  dependsOn("autoconfigure")
}

tasks.named<de.mclsg.task.IntellijPluginTask>("generateMontiArcWithCD4AIntellijPlugin") {
  handCodedDir.set(project.layout.projectDirectory.dir("main/java"))
}

tasks.named("patchPluginXml") {
  dependsOn("generateMontiArcWithCD4AIntellijPlugin")
}

tasks.named("compileJava") {
  dependsOn("generateMontiArcWithCD4AIntellijPlugin")
}

tasks.named("sourcesJar") {
  dependsOn("generateMontiArcWithCD4AIntellijPlugin")
}

tasks.named("buildPlugin") {
  dependsOn("prepareSandbox")
}

tasks.named<org.jetbrains.intellij.platform.gradle.tasks.PrepareSandboxTask>("prepareSandbox") {
  // TODO: double copy, we can probably skip copy into generated-sources
  from("build/generated-sources/MontiArcWithCD4A/plugins/montiarcwithcd4a-intellij-plugin/bin/") {
    into("montiarc-intellij-plugin/bin") // based on project name, not generated plugin name!
  }
  dependsOn("copyMontiArcWithCD4AJarIntoIntellijPlugin")
}

tasks.named("copyMontiArcWithCD4AJarIntoIntellijPlugin"){
  dependsOn(":tooling:language-server:packMontiArcWithCD4ALanguageServer")
}
