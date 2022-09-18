/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.language-server")
}

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }
  grammar(project(path = ":languages", configuration = "grammars"))
  grammar("${libs.monticoreStatecharts}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.scGrammarsCapability) }
  }
  implementation(project(":languages:montiarc"))
  implementation("de.monticore.language-server:monticore-language-server-runtime:${libs.monticoreLSPVersion}")
}

// create needs to be used instead of register, since register is evaluated lazily and this too late,
// since this task creates other tasks
val autoconfigure = tasks.create<de.mclsg.task.AutoconfigureTask>("autoconfigure") {
  configure<de.mclsg.MCLSGPluginExtension> {
    languageName = "MontiArc"
    fileExtension = "arc"
    setHandCodedDirBase("${projectDir}/main")
  }
  including(
      de.mclsg.TaskTypes.INTELLIJ_PLUGIN
  )
  autoconfigureLspTasks()
}
tasks.getByName("generateMontiArcLanguageServer") { dependsOn(project(":languages").tasks.getByName("grammarJar")) }
val t = tasks.getByName("packMontiArcLanguageServer")
project(":languages"){ subprojects{ t.dependsOn (tasks.getByName("assemble")) }}