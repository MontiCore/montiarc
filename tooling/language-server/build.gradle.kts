/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.language-server")
}

val cd4aLib = libs.mc.cd4a
dependencies {
  grammar(libs.mc.grammar) {
    capabilities { requireCapability("de.monticore:monticore-grammar-grammars") }
  }
  grammar(cd4aLib.get().module.group, cd4aLib.get().module.name, cd4aLib.get().versionConstraint.requiredVersion, classifier = "grammars")
  grammar(project(path = ":languages", configuration = "grammars"))
  grammar(libs.mc.sc) {
    capabilities { requireCapability("de.monticore.lang:statecharts-grammars") }
  }
  implementation(project(":languages:montiarc"))
  implementation(libs.mc.lsp)
  implementation(cd4aLib)
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
}
tasks.getByName("generateMontiArcWithCD4ALanguageServer") { dependsOn(project(":languages").tasks.getByName("grammarJar")) }
val t = tasks.getByName("packMontiArcWithCD4ALanguageServer")

project(":languages"){ subprojects{ t.dependsOn (tasks.getByName("assemble")) }}