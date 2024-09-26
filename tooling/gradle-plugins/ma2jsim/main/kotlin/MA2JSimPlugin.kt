/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.ma2jsim

import montiarc.gradle.montiarc.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "maGenerator"

const val MA_TOOL_CLASS = "montiarc.generator.MA2JSimTool"

const val INTERNAL_GENERATOR_PROJECT_REF = ":generators:ma2jsim"
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:ma2jsim"

const val INTERNAL_RTE_PROJECT_REF = ":libraries:simulator-rte"
const val MAVEN_RTE_PROJECT_REF = "montiarc.libraries:simulator-rte"

const val INTERNAL_TEST_RTE_PROJECT_REF = ":libraries:simulator-test-rte"
const val MAVEN_TEST_RTE_PROJECT_REF = "montiarc.libraries:simulator-test-rte"

const val INTERNAL_MA_BASE_PROJECT_REF = ":libraries:montiarc-base"
const val MAVEN_MA_BASE_PROJECT_REF = "montiarc.libraries:montiarc-base"

const val MA2JSIM_LOGGING_ENV_VAR = "MA2JSIM_LOGGING_BASE_PATH"

/**
 * Enables the integration of montiarc models into a project build:
 * Declare the directories in which montiarc models lay, let them be compiled to java
 * and distributed so that others can use these models, too.
 * @see MontiArcCompile
 */
@Suppress("unused")
class Ma2JavaPlugin : Plugin<Project> {

  private lateinit var project: Project
  private lateinit var maExtension: MAExtension

  override fun apply(project: Project){
    this.project = project
    this.project.pluginManager.apply(MontiarcBasePlugin::class.java)
    this.maExtension = project.extensions.getByType(MAExtension::class.java)

    with (project) {
      addGeneratorDependency()

      sourceSetsOf(project).all { sourceSet ->
        // Enabling the declaration of model dependencies and prepare their extraction
        createCompileMontiarcTask(sourceSet)
        addRuntimeEnvironmentDependencyFor(sourceSet)
        addMontiArcBaseDependencyFor(sourceSet)
      }

      // Special treatments for the main and test source sets. They only exist, if the java plugin is applied
      pluginManager.withPlugin("java") {
        makeMainModelsAvailableInTests()

        val mainSourceSet = sourceSetsOf(project).getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        putCompiledSymbolsIntoJarOf(mainSourceSet)

        val testSourceSet = sourceSetsOf(project).getByName(SourceSet.TEST_SOURCE_SET_NAME)
        addTestRteDependencyFor(testSourceSet)

        // Also special treatment for the test task
        addLoggingEnvVarToTestTask()
      }

      pluginManager.withPlugin("cd2pojo") {
        pluginManager.apply(CDOut2MAInPlugin::class.java)
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
    configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // We do not use this configuration for publishing
    }

    // Add a dependency on the ma2java jar. Depending on what the user wishes, ma2java may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of ma2java.
    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider {
      if(maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_GENERATOR_PROJECT_REF)
      } else {
        "${MAVEN_GENERATOR_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  private fun addRuntimeEnvironmentDependencyFor(sourceSet: SourceSet) = with(project) {
    addRuntimeEnvironmentDependencyFor(sourceSet.implementationConfigurationName)

    // If the project is a library and gets consumed, the consumer must transitively consume the runtime environment,
    // too. Therefore, we want to put the dependency on the api configuration. However, the api configuration only
    // exists for the main source set. Moreover, we also want to support users of the normal java plugin that does not
    // have api configurations. It only has implementation configurations which we then alternatively use.
    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        addRuntimeEnvironmentDependencyFor(sourceSet.apiConfigurationName)
      }
    }
  }

  private fun addRuntimeEnvironmentDependencyFor(configName: String) = with (project) {
    // Depending on what the user wishes, ma2jsim-rte may be drawn from maven (default), or it may be an internal project
    // dependency. This only makes sense for us, the MontiArc developers, because this way we can directly test the
    // freshly compiled version of ma2jsim-rte.
    dependencies.addProvider(configName, provider {
      if (maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_RTE_PROJECT_REF)
      } else {
        "${MAVEN_RTE_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  /**
   * Adds the library montiarc.libraries:montiarc-test-rte as implementation dependency for the source set
   */
  private fun addTestRteDependencyFor(sourceSet: SourceSet) = with (project) {
    val configName = sourceSet.implementationConfigurationName
    // Depending on what the user wishes, ma2jsim-test-rte may be drawn from maven (default), or it may be an internal project
    // dependency. This only makes sense for us, the MontiArc developers, because this way we can directly test the
    // freshly compiled version of ma2jsim-test-rte.
    dependencies.addProvider(configName, provider {
      if (maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_TEST_RTE_PROJECT_REF)
      } else {
        "${MAVEN_TEST_RTE_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  /**
   * Adds the library montiarc.libraries:montiarc-base as montiarc dependency for the source set
   */
  private fun addMontiArcBaseDependencyFor(sourceSet: SourceSet) = with(project) {
    // Depending on what the user wishes, montiarc-base may be drawn from maven (default), or it may be an internal
    // project dependency. This only makes sense for us, the MontiArc developers, because this way we can directly test
    // the freshly compiled version of montiarc-base.

    dependencies.addProvider(sourceSet.montiarcDependencyDeclarationConfigName, provider {
      if (maExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_MA_BASE_PROJECT_REF)
      } else {
        "${MAVEN_MA_BASE_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  /**
   * Create a task that compiles the MontiArc sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the java sources of the same SourceSet.
   */
  private fun createCompileMontiarcTask(sourceSet: SourceSet): TaskProvider<MontiArcCompile> = with (project) {
    val montiarcSrcDirSet = sourceSet.montiarc.get()
    val taskName = sourceSet.compileMontiarcTaskName
    val generateTask = tasks.register(taskName, MontiArcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the Montiarc models in source set ${sourceSet.name}."

      genTask.modelPath.from(montiarcSrcDirSet.sourceDirectories)
      genTask.outputDir.set(montiarcSrcDirSet.destinationDirectory)
      genTask.symbolImportDir.from(
        configurations.named(sourceSet.montiarcSymbolDependencyConfigurationName),
        configurations.named(sourceSet.cd2Pojo4MaSymbolDependencyConfigName)
      )

      sourceSet.java.srcDir(genTask.javaOutputDir())
      genTask.hwcPath.from( provider {
        sourceSet.allJava.sourceDirectories.files
        .filter { !it.startsWith(buildDir)}
      })
      genTask.setIgnoreExitValue(true)
    }

    sourceSet.montiarc.get().compiledBy(generateTask, MontiArcCompile::outputDir)
    setTaskOrderAfterMaGenerator(generateTask)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }

  /**
   * If [MAExtension.internalMontiArcTesting] is true, then this method schedules the [MontiarcCompile] task to run
   * after the tests of the generator finished.
   */
  private fun setTaskOrderAfterMaGenerator(generateTask: TaskProvider<MontiArcCompile>) = with (project) {
    generateTask.configure { genTask ->
      if (maExtension.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project(INTERNAL_GENERATOR_PROJECT_REF).tasks.withType(Test::class.java))
      }
    }
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

    val mainCompile = tasks.named(mainSourceSet.compileMontiarcTaskName, MontiArcCompile::class.java)
    // Puts main's symbols on the symbol path of test
    tasks.named(testSourceSet.compileMontiarcTaskName, MontiArcCompile::class.java) {
      it.symbolImportDir.from(mainCompile.get().symbolOutputDir())
    }
  }

  private fun addLoggingEnvVarToTestTask() = with (project) {
    val logDir = "$buildDir/montiarc/test/logs"
    tasks.withType(Test::class.java).forEach {
      it.environment(MA2JSIM_LOGGING_ENV_VAR, logDir)
      it.outputs.dir(logDir)
    }
  }

  /**
   * Configures the symbols jar task so that it contains the symbols produced by the [MontiArcCompile] task.
   */
  private fun putCompiledSymbolsIntoJarOf(sourceSet: SourceSet) = with (project) {
    val compileTask = tasks.named(sourceSet.compileMontiarcTaskName, MontiArcCompile::class.java)
    tasks.named(sourceSet.montiarcSymbolsJarTaskName, Jar::class.java).configure {jar ->
      jar.from(compileTask.get().symbolOutputDir())
    }
  }
}
