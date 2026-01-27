/* (c) https://github.com/MontiCore/monticore */
import de.mclsg.task.AutoconfigureTask
import de.mclsg.task.LanguageServerJarTask
import de.mclsg.task.MCLSGAggregationTask
import de.mclsg.task.MCLSGTask
import de.mclsg.task.RunVscodePluginAttachedTask
import de.mclsg.task.VscodePluginTask
import de.monticore.gradle.dependencies.MCSourceSets
import montiarc.build.VscodeGenConfig
import org.gradle.accessors.dm.LibrariesForSeLibs

plugins {
  id("montiarc.build.java")
  id("montiarc.build.nodejs")

  id("de.monticore.language-server")
}

//https://github.com/gradle/gradle/issues/15383
val seLibs = the<LibrariesForSeLibs>()

// Provide a more convenient way to declare resources to be included in the vscode extension
val vsResources = extensions.create("vscodeResources", VscodeGenConfig::class.java)

val copyVsResources = tasks.register<Copy>("copyVscodeResources") {
  from(vsResources.readme)
  from(vsResources.languageConfig)
  from(vsResources.icon) {
    into("icons")  // Subdirectory of the general "into" directory
  }

  into(vsResources.extensionProjectLocation)
}

// The LSP plugin requires a "grammar" and "grammarSymbolDependencies"
// configuration. The "grammar" configuration is created by the LSP plugin, but
// insufficiently configured. The "grammarSymbolDependencies" configuration is
// missing. To avoid the application of the general monticore plugin, we
// catch up with these shortcomings.
// NOTE however, that the configuration / creation of these grammars makes this
// plugin incompatible with the monticore plugin. So if you want to apply it
// too, than remove this code from this plugin.
pluginManager.withPlugin("de.monticore.generator") {
  throw GradleException(
    """
    Applied plugins 'de.monticore.generator' and 'montiarc.build.language-server'
    are incompatible with each other. See the build-logic plugin
    'montiarc.build.language-server' for more details.
    """.trimIndent()
  )
}

val grammarDeclConfig = configurations.named("grammar") {
  isCanBeResolved = false
  isCanBeConsumed = false
  isVisible = true

  description = "Declaration of languages which the LSP should cover"
}

val grammarContentConfig = configurations.resolvable("grammarSymbolDependencies") {
  extendsFrom(grammarDeclConfig.get())

  description = "Resolves (locates and downloads) the grammars which the LSP should cover"

  MCSourceSets.addSymbolJarAttributes(this, project)
}
// End declaration of grammar configurations


// If java plugin is applied:
// Pull java implementations of declared grammars automatically.
// Moreover: add lsp runtime as java dependency.
// Lastly: The fat Jar task does not declare an explicit dependency on all java
//         dependencies, although they are consumed by it. This confuses Gradle.
//         -> Explicitly declare them
pluginManager.withPlugin("java") {
  configurations.named("implementation").configure {
    extendsFrom(grammarDeclConfig.get())
  }

  dependencies.add("implementation", seLibs.mc.lsp);

  tasks.withType<LanguageServerJarTask> {
    inputs.files(configurations.named("runtimeClasspath"))
  }
}



// Fix for MC tasks that do not declare the grammarSymbolDependencies(...) config as input
tasks.withType<MCLSGAggregationTask> {
  inputs.files(grammarContentConfig)
}

tasks.withType<MCLSGTask> {
  inputs.files(grammarContentConfig)
}

// If we are building a multi-project lang server, set the option for vscode
// runners
val multiProjectArgProvider =
  extensions
    .getByType<VscodeGenConfig>()
    .multiproject
    .map { if (it) { "-mup" } else { "" } }

tasks.withType<RunVscodePluginAttachedTask> {
  doFirst {
    args.add(multiProjectArgProvider.get())
  }
}

tasks.withType<VscodePluginTask> {
  additionalCliArgs.add(multiProjectArgProvider)
}

// Add additional task dependencies
tasks.withType<VscodePluginTask> {
  finalizedBy("editPackageJson", "copyVscodeResources")
}

tasks.build {
  dependsOn(tasks.withType<AutoconfigureTask>())
}
