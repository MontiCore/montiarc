/* (c) https://github.com/MontiCore/monticore */
/*plugins {
  id("montiarc.build.language-server")
}


dependencies {
  grammar(project(":languages:montiarc"))
  grammar(libs.mc.grammar)
  grammar(libs.mc.cd4a)
  grammar(libs.mc.sc)

  implementation(project(":languages:montiarc"))
  implementation(libs.mc.lsp)
  implementation(libs.mc.cd4a)
  implementation(libs.mc.c2mc)
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
  autoconfigureLspTasks()
}*/
