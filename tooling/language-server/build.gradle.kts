/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.language-server")
}

dependencies {
  grammar("${libs.monticoreGrammar}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.mcGrammarsCapability) }
  }
  grammar("${libs.cd4analysis}:${libs.monticoreVersion}"){
    capabilities { requireCapability(libs.cd4aGrammarsCapability) }
  }
  grammar(project(path = ":languages", configuration = "grammars"))
  grammar("${libs.monticoreStatecharts}:${libs.monticoreVersion}") {
    capabilities { requireCapability(libs.scGrammarsCapability) }
  }
  implementation(project(":languages:montiarc"))
  implementation("de.monticore.language-server:monticore-language-server-runtime:${libs.monticoreLSPVersion}")
  implementation("${libs.cd4analysis}:${libs.monticoreVersion}")
  implementation("${libs.monticoreClass2MC}:${libs.monticoreVersion}")
}

// create needs to be used instead of register, since register is evaluated lazily and this too late,
// since this task creates other tasks
val autoconfigure = tasks.create<de.mclsg.task.AutoconfigureTask>("autoconfigure") {
  configure<de.mclsg.MCLSGPluginAggregationExtension> {
    setLanguageAggregationName("MontiArcWithCD4A")
    setTargetPackage("montiarc_with_cd4a")
    setHandCodedDirBase("${projectDir}/main")

    member("MontiArc", "arc", true)
    member("de.monticore.CD4Analysis", "cd", true)
  }
  including(
      de.mclsg.TaskTypes.INTELLIJ_PLUGIN
  )
  autoconfigureLspTasks()
}
tasks.getByName("generateMontiArcWithCD4ALanguageServer") { dependsOn(project(":languages").tasks.getByName("grammarJar")) }
val t = tasks.getByName("packMontiArcWithCD4ALanguageServer")

project(":languages"){ subprojects{ t.dependsOn (tasks.getByName("assemble")) }}