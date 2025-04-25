/* (c) https://github.com/MontiCore/monticore */
plugins {
  id("montiarc.build.language-server")
}

configurations {
  grammar
}

dependencies {
  grammar(project(":languages:montiarc"))
  grammar(libs.mc.grammar)
  grammar(libs.mc.cd4a)
  grammar(libs.mc.sc)

  implementation(project(":languages:montiarc"))
  implementation(libs.mc.grammar)
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
  including(
    //de.mclsg.TaskTypes.INTELLIJ_PLUGIN,
    de.mclsg.TaskTypes.VSCODE_PLUGIN
  )
  autoconfigureLspTasks()
}

tasks.named("build") {
  dependsOn("autoconfigure")
}

// Edit package.json of the generated project
tasks.register("editPackageJson") {
  doLast {
    val file = File(
      "${
        autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir()
      }/package.json"
    )
    val json = groovy.json.JsonBuilder(groovy.json.JsonSlurper().parse(file))
    val content = json.content as MutableMap<String, Any>
    content["version"] = version.toString()
    content["name"] = "montiarc"
    content["displayName"] = "MontiArc"
    content["icon"] = "icons/icon.png"
    content["license"] = "SEE LICENSE IN LICENSE.txt"
    content["homepage"] = "https://github.com/MontiCore/montiarc/blob/dev/README.md"
    content["preview"] = true
    content["repository"] = mapOf(
      "type" to "git",
      "url" to "https://github.com/MontiCore/montiarc"
    )


    val languageContributes =
      (((content["contributes"] as Map<*, *>)["languages"] as List<*>)[0] as MutableMap<String, Any>)
    languageContributes["icon"] =
      mapOf(
        "light" to "./icons/icon.png",
        "dark" to "./icons/icon.png"
      )
    languageContributes["configuration"] = "./language-configuration.json"

    (content["contributes"] as MutableMap<String, Any>)["grammars"] = listOf(
      mapOf(
        "language" to "MontiArcWithCD4A",
        "scopeName" to "source.montiarc",
        "path" to "./montiarc.tmLanguage.json",
        "embeddedLanguages" to {
          "meta.embedded.block.java" to "source.java"
        }
      )
    )

    (content["contributes"] as MutableMap<String, Any>)["snippets"] = listOf(
      mapOf(
        "language" to "MontiArcWithCD4A",
        "path" to "./snippets.json",
      )
    )

    file.writeText(json.toPrettyString())
  }
}

tasks.register<Copy>("copyIcon") {
  from(rootProject.projectDir.absolutePath + "/docs/img/icon.png")
  include("icon.png")
  into(autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir() + "/icons")
}

tasks.register<Copy>("copyConfiguration") {
  from(projectDir.absolutePath + "/main/resources/")
  include("language-configuration.json")
  include("montiarc.tmLanguage.json")
  include("snippets.json")
  into(autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir())
}

tasks.register<Copy>("copyReadme") {
  from(rootProject.projectDir)
  include("README.md")
  into(autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir())
}

tasks.named("generateMontiArcWithCD4AVscodePlugin") {
  finalizedBy("editPackageJson", "copyIcon", "copyConfiguration", "copyReadme")
}

tasks.named<Exec>("buildMontiArcWithCD4AVscodePlugin") {
  dependsOn(project.tasks.npmInstall)
  addNpmToPath(this)
}

tasks.named<Exec>("packageMontiArcWithCD4AVscodePlugin") {
  dependsOn(project.tasks.npmInstall)
  addNpmToPath(this)
}

fun addNpmToPath(task: Exec) {
  val isWindows = System.getProperty("os.name").toLowerCase().startsWith("win")
  if (isWindows) {
    task.environment(
      "PATH",
      "${project.node.computedNodeDir.get()}${File.pathSeparator}${System.getenv("PATH")}"
    )
  } else {
    task.environment(
      "PATH",
      "${project.node.computedNodeDir.get()}/bin${File.pathSeparator}${System.getenv("PATH")}"
    )
  }
}
