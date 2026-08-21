/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.cd2pojo.VERSION
import montiarc.gradle.montiarc.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

const val TOOL_CLASSPATH_CONFIG_NAME = "ma2jsimToolClasspath"

const val MA_TOOL_CLASS = "montiarc.generator.MA2JSimTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:ma2jsim:${VERSION}"

const val MAVEN_RTE_PROJECT_REF = "montiarc.libraries:simulator-rte:${VERSION}"

const val MAVEN_TEST_RTE_PROJECT_REF = "montiarc.libraries:simulator-test-rte:${VERSION}"

const val MAVEN_MA_BASE_PROJECT_REF = "montiarc.libraries:montiarc-base:${VERSION}"

const val MAVEN_MAUNIT_PROJECT_REF = "montiarc.libraries:maunit:${VERSION}"

const val MA2JSIM_LOGGING_ENV_VAR = "MA2JSIM_LOGGING_BASE_PATH"


/**
 * Enables the integration of montiarc models into a project build:
 * Declare the directories in which montiarc models lay, let them be compiled to java
 * and distributed so that others can use these models, too.
 * @see MontiArcCompile
 */
@Suppress("unused")
class MA2JSimPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project){
    this.project = project
    this.project.pluginManager.apply(MontiArcBasePlugin::class.java)

    this.project.extensions.extraProperties.set("MATaskType", MontiArcCompile::class.java)

    with (project) {
      addGeneratorDependency()

      sourceSetsOf(project).all { sourceSet ->
        // Enabling the declaration of model dependencies and prepare their extraction
        createCompileMontiArcTask(sourceSet)
        addRuntimeEnvironmentDependencyFor(sourceSet)
        dependencies.addProvider(sourceSet.montiarcConfigName, provider { MAVEN_MA_BASE_PROJECT_REF })
        dependencies.addProvider(sourceSet.montiarcConfigName, provider { MAVEN_MAUNIT_PROJECT_REF })
      }

      // Special treatments for the main and test source sets. They only exist, if the java plugin is applied
      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()

        val mainSourceSet = sourceSetsOf(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        putCompiledSymbolsIntoJarOf(mainSourceSet)

        val testSourceSet = sourceSetsOf(project).getByName(SourceSet.TEST_SOURCE_SET_NAME)
        dependencies.addProvider(testSourceSet.implementationConfigurationName, provider { MAVEN_TEST_RTE_PROJECT_REF })

        // Also special treatment for the test task
        addLoggingEnvVarToTestTask()
      }

      pluginManager.withPlugin("java-test-fixtures") {
        makeTestFixturesModelsAvailableInTests()
      }

      pluginManager.withPlugin("cd2pojo") {
        pluginManager.apply(CDOut2MAInPlugin::class.java)
      }

      pluginManager.withPlugin("fmu2arc") {
        pluginManager.apply(FMUOut2MAInPlugin::class.java)
      }
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  private fun addGeneratorDependency() = with (project) {
    // Add a configuration to store the classpath for executing the ma2java generator
    configurations.create(TOOL_CLASSPATH_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // The configuration should not be published, its internal to the compile task impl
      it.isCanBeDeclared = false // The user should not be able to declare new dependencies in this configuration
      it.isVisible = false // Should not be visible outside the project
    }

    // Add a dependency on the ma2jsim jar
    dependencies.addProvider(TOOL_CLASSPATH_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
  }

  private fun addRuntimeEnvironmentDependencyFor(sourceSet: SourceSet) = with(project) {
    dependencies.addProvider(sourceSet.implementationConfigurationName, provider { MAVEN_RTE_PROJECT_REF })
    dependencies.addProvider(sourceSet.fmu2arc4montiarcConfigName, provider { MAVEN_RTE_PROJECT_REF })

    // If the project is a library and gets consumed, the consumer must transitively consume the runtime environment,
    // too. Therefore, we want to put the dependency on the api configuration. However, the api configuration only
    // exists for the main source set. Moreover, we also want to support users of the normal java plugin that does not
    // have api configurations. It only has implementation configurations which we then alternatively use.
    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        dependencies.addProvider(sourceSet.apiConfigurationName, provider { MAVEN_RTE_PROJECT_REF })
      }
    }
  }

  /**
   * Create a task that compiles the MontiArc sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the java sources of the same SourceSet.
   */
  private fun createCompileMontiArcTask(sourceSet: SourceSet): TaskProvider<MontiArcCompile> = with (project) {
    val montiarcSrcDirSet = sourceSet.montiarc.get()
    val taskName = sourceSet.compileMontiArcTaskName
    val generateTask = tasks.register(taskName, MontiArcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the MontiArc models in source set ${sourceSet.name}."

      genTask.modelpath.from(montiarcSrcDirSet.sourceDirectories)
      genTask.outputDir.set(montiarcSrcDirSet.destinationDirectory)
      genTask.symbolpath.from(
        configurations.named(sourceSet.montiarcSymbolpathConfigName),
        configurations.named(sourceSet.cd2pojo4montiarcSymbolpathConfigName),

        // Fmu Symbols
        configurations.named(sourceSet.fmu2arc4montiarcSymbolpathConfigName),
        // Fmu dependent Component Symbols
        configurations.named(sourceSet.fmu2arc4montiarcCompSymbolpathConfigName)
      )

      sourceSet.java.srcDir(genTask.javaOutputDir())
      genTask.hwcPath.setFrom(provider {
        sourceSet.allJava.sourceDirectories.files
          .filter { !it.startsWith(layout.buildDirectory.get().asFile) }
      })
    }

    sourceSet.montiarc.get().compiledBy(generateTask, MontiArcCompile::outputDir)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }

  /**
   * Makes symbols of source set `main`'s compiled models available in `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] is applied).
   */
  private fun makeMainModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java")) {
      logger.error("Internal error: Tried to link main and test source sets, but the JavaPlugin is not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val mainSourceSet = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val mainCompile = tasks.named(mainSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java) {
      it.symbolpath.from(mainCompile.get().symbolOutputDir())
    }
  }

  private fun addLoggingEnvVarToTestTask() = with (project) {
    val logDir = layout.buildDirectory.dir("montiarc/test/logs")
    tasks.withType(Test::class.java).forEach {
      it.environment(MA2JSIM_LOGGING_ENV_VAR, logDir.get().asFile.absolutePath)
      it.outputs.dir(logDir)
    }
  }

  /**
   * Configures the symbols jar task so that it contains the symbols produced by the [MontiArcCompile] task.
   */
  private fun putCompiledSymbolsIntoJarOf(sourceSet: SourceSet) = with (project) {
    val compileTask = tasks.named(sourceSet.compileMontiArcTaskName, MontiArcCompile::class.java)
    tasks.named(sourceSet.montiarcSymbolsJarTaskName, Jar::class.java).configure {jar ->
      jar.from(compileTask.get().symbolOutputDir())
    }
  }

  /**
   * Makes the compiled java source set `testFixtures` available in `test` (these source sets must exist, checked by
   * whether the [org.gradle.api.plugins.JavaPlugin] and [org.gradle.api.plugins.JavaTestFixturesPlugin] is applied).
   */
  private fun makeTestFixturesModelsAvailableInTests() = with (project) {
    if (!pluginManager.hasPlugin("java") || !pluginManager.hasPlugin("java-test-fixtures")) {
      logger.error("Internal error: Tried to link testFixtures and test source sets, but the Java and JavaTestFixturesPlugin are not applied!")
    }

    val sourceSets = extensions.getByType(JavaPluginExtension::class.java).sourceSets
    val testSourceSet = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

    val compileTestFixturesJava = tasks.named("compileTestFixturesJava", JavaCompile::class.java)
    // Puts test fixtures on the symbol path of test
    tasks.named(testSourceSet.compileMontiArcTaskName, MontiArcCompile::class.java) {
      it.symbolpath.from(compileTestFixturesJava.get().destinationDirectory)
    }
  }
}
