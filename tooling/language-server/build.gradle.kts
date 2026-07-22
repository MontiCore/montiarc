/* (c) https://github.com/MontiCore/monticore */
import montiarc.build.VscodeGenConfig
import java.io.File

plugins {
  id("montiarc.build.language-server")
}

dependencies {
  grammar(project(":languages:montiarc"))
  grammar(seLibs.mc.cd4a)

  implementation(libs.gradle.tooling.api)
  implementation(seLibs.mc.c2mc)
  implementation(variantOf(seLibs.mc.cd4a) { classifier("language-server") })
}

val languageServerJarArtifactConfig = configurations.register("languageServerJar") {
  isCanBeConsumed = true
  isCanBeResolved = false
}

afterEvaluate {
  languageServerJarArtifactConfig.configure {
    outgoing.artifact(tasks.named("packMontiArcWithCDLanguageServer")) {
      builtBy(tasks.named("packMontiArcWithCDLanguageServer"))
    }
  }
}

tasks.configureEach {
  enabled = enabled && project.hasProperty("enableLanguageServer")
}

java {
  withSourcesJar()
}

// Eager task creation because the task creates other tasks
val autoconfigure = tasks.create<de.mclsg.task.AutoconfigureTask>("autoconfigure") {
  configure<de.mclsg.MCLSGPluginAggregationExtension> {
    setLanguageAggregationName("MontiArcWithCD")
    setTargetPackage("montiarc_with_cd")
    setHandCodedDirBase("${projectDir}/main")

    member("MontiArc", "arc", true)
    member("de.monticore.CD4Code", "cd", false)
  }
  including(
    de.mclsg.TaskTypes.LANGUAGE_SERVER,
    de.mclsg.TaskTypes.VSCODE_PLUGIN
  )
  autoconfigureLspTasks()
}

extensions.configure<VscodeGenConfig>(VscodeGenConfig::class) {
  multiproject.set(true)
  icon.set(rootProject.file("docs/assets/images/icon.png"))
  readme.set(rootProject.file("README.md"))
  languageConfig.from(
    projectDir.absolutePath + "/main/resources/" + "language-configuration.json",
    projectDir.absolutePath + "/main/resources/" + "montiarc.tmLanguage.json",
    projectDir.absolutePath + "/main/resources/" + "snippets.json"
  )
  extensionProjectLocation.set(
    file(
      autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir()
    )
  )
}

// Extract these values at configuration time
val projectVersion = version.toString()
val packageJsonFile = File(autoconfigure.getMclsgPluginAggregationExtension().getFullVscodePluginDir(), "package.json")

tasks.register("editPackageJson") {
  val targetFile = packageJsonFile
  val targetVersion = projectVersion

  doLast {
    val json = groovy.json.JsonBuilder(groovy.json.JsonSlurper().parse(targetFile))
    val content = json.content as MutableMap<String, Any>
    content["version"] = targetVersion
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
        "language" to "MontiArcWithCD",
        "scopeName" to "source.montiarc",
        "path" to "./montiarc.tmLanguage.json",
        "embeddedLanguages" to mapOf(
          "meta.embedded.block.java" to "source.java"
        )
      )
    )

    (content["contributes"] as MutableMap<String, Any>)["snippets"] = listOf(
      mapOf(
        "language" to "MontiArcWithCD",
        "path" to "./snippets.json",
      )
    )

    (content["contributes"] as MutableMap<String, Any>)["menus"] = mapOf(
      "editor/title" to listOf(
        mapOf(
          "when" to "editorLangId == 'MontiArcWithCD'",
          "command" to "gradle.runBuild",
          "group" to "navigation@1",
        )
      )
    )

    targetFile.writeText(json.toPrettyString())
  }
}

// Extract value at configuration time
val nodeExecutableDir = project.node.resolvedNodeDir.asFile.get()

fun addNpmToPath(task: Exec, nodeDir: File) {
  val isWindows = System.getProperty("os.name").lowercase().startsWith("win")
  val sysPath = System.getenv("PATH")
  val pathSep = File.pathSeparator

  if (isWindows) {
    task.environment("PATH", "${nodeDir}${pathSep}${sysPath}")
  } else {
    task.environment("PATH", "${nodeDir}/bin${pathSep}${sysPath}")
  }
}

tasks.named<Exec>("buildMontiArcWithCDVscodePlugin") {
  dependsOn(
    tasks.named("npmInstall"),
    "packageMontiArcWithCDVscodePlugin"
  )
  addNpmToPath(this, nodeExecutableDir)
}

tasks.named<Exec>("packageMontiArcWithCDVscodePlugin") {
  dependsOn(
    tasks.named("npmInstall"),
    "editPackageJson",
    "copyVscodeResources"
  )
  addNpmToPath(this, nodeExecutableDir)
}

tasks.named<Jar>("sourcesJar") {
  dependsOn(tasks.named("generateMontiArcWithCDLanguageServer"))
}
