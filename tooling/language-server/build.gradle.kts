/* (c) https://github.com/MontiCore/monticore */
import de.mclsg.task.RunVscodePluginAttachedTask
import de.mclsg.task.VscodePluginTask

plugins {
  id("montiarc.build.language-server")
}

configurations {
  grammar
}

dependencies {
  grammar(project(":languages:montiarc"))
  grammar(seLibs.mc.grammar)
  grammar(seLibs.mc.cd4a)
  grammar(seLibs.mc.statecharts)

  implementation(project(":languages:montiarc"))
  implementation(libs.gradle.tooling.api)
  implementation(seLibs.mc.grammar)
  implementation(seLibs.mc.lsp)
  implementation(seLibs.mc.cd4a)
  implementation(seLibs.mc.c2mc)
  implementation(variantOf(seLibs.mc.cd4a) { classifier("language-server") })
}

val languageServerJarArtifactConfig = configurations.register("languageServerJar") {
  isCanBeConsumed = true
  isCanBeResolved = false
}

afterEvaluate {
  languageServerJarArtifactConfig.configure {
    outgoing.artifact(tasks.named("packMontiArcWithCD4ALanguageServer")) {
      builtBy(tasks.named("packMontiArcWithCD4ALanguageServer"))
    }
  }
}

tasks.configureEach {
  enabled = enabled && project.hasProperty("enableLanguageServer")
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
  }
  including(
    de.mclsg.TaskTypes.LANGUAGE_SERVER,
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

    (content["contributes"] as MutableMap<String, Any>)["menus"] = mapOf(
      "editor/title" to listOf(
        mapOf(
          "when" to "editorLangId == 'MontiArcWithCD4A'",
          "command" to "gradle.runBuild",
          "group" to "navigation@1",
        )
      )
    )

    file.writeText(json.toPrettyString())
  }
}

tasks.register<Copy>("copyIcon") {
  from(rootProject.projectDir.absolutePath + "/docs/assets/images/icon.png")
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

tasks.named<VscodePluginTask>("generateMontiArcWithCD4AVscodePlugin") {
  this.getAdditionalCliArgs().add("-mup")
  finalizedBy("editPackageJson", "copyIcon", "copyConfiguration", "copyReadme")
}

tasks.named<RunVscodePluginAttachedTask>("runMontiArcWithCD4AVscodePluginAttached") {
  this.args("-mup")
}

tasks.named<Exec>("buildMontiArcWithCD4AVscodePlugin") {
  dependsOn(project.tasks.npmInstall, "packageMontiArcWithCD4AVscodePlugin")
  addNpmToPath(this)
}

tasks.named<Exec>("packageMontiArcWithCD4AVscodePlugin") {
  dependsOn(project.tasks.npmInstall, "editPackageJson", "copyIcon", "copyConfiguration", "copyReadme")
  addNpmToPath(this)
}

tasks.named("generateMontiArcWithCD4ALanguageServer") {
  dependsOn(tasks.generateMCGrammars)
}

tasks.named("sourcesJar") {
  dependsOn(tasks.named("generateMontiArcWithCD4ALanguageServer"))
}

tasks.named("packMontiArcWithCD4ALanguageServer") {
  dependsOn(
    ":languages:montiarc:jar",
    ":languages:basis:jar",
    ":languages:automaton:jar",
    ":languages:comfy:jar",
    ":languages:compute:jar",
    ":languages:features:jar",
    ":languages:modes:jar"
  )
}

fun addNpmToPath(task: Exec) {
  val isWindows = System.getProperty("os.name").lowercase().startsWith("win")
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
