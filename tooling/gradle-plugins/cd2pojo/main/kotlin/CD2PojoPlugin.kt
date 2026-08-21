/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.lambdas.SerializableLambdas.spec
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider

/**
 * Name of the configuration containing the CD2Pojo tool and its runtime dependencies.
 */
const val TOOL_CLASSPATH_CONFIG_NAME = "cd2pojoToolClasspath"

/**
 * Fully qualified name of the CD2Pojo tool entry point.
 */
const val CD2POJO_TOOL_CLASS = "de.monticore.cd2pojo.CD2PojoTool"

/**
 * Dependency coordinates of the CD2Pojo generator.
 */
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:cd2pojo:${VERSION}"

/**
 * Dependency coordinates of the SE-RWTH logging library required by generated code.
 */
const val SE_LOGGING_PROJECT_REF = "de.se_rwth.commons:se-commons-logging:${VERSION}"

class CD2PojoPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    this.project.extensions.extraProperties.set("CDTaskType", CD2PojoCompile::class.java)

    with(project) {
      pluginManager.apply("java-base")

      configureToolClasspath()

      project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { sourceSet ->
        addCD2PojoEntryToSourceSet(sourceSet)
        createCompileCD2PojoTask(sourceSet)

        dependencies.addProvider(
          sourceSet.implementationConfigurationName,
          provider { SE_LOGGING_PROJECT_REF }
        )
      }

      pluginManager.apply(CD2PojoDistributionPlugin::class.java)
    }
  }

  /**
   * Creates and configures the classpath used to launch the CD2Pojo tool.
   */
  private fun configureToolClasspath() = with(project) {
    configurations.create(TOOL_CLASSPATH_CONFIG_NAME) {
      it.isCanBeResolved = true
      it.isCanBeConsumed = false
      it.isVisible = false
    }

    dependencies.addProvider(
      TOOL_CLASSPATH_CONFIG_NAME,
      provider { MAVEN_GENERATOR_PROJECT_REF }
    )
  }

  /**
   * Registers the `cd2pojo` source directory set for [sourceSet].
   *
   * Its default source directory is `src/<sourceSet>/cd2pojo`; its generated output
   * is placed under `build/cd2pojo/<sourceSet>`. The source set is registered as part
   * of [SourceSet.getAllSource] and excluded from resource processing.
   */
  private fun addCD2PojoEntryToSourceSet(sourceSet: SourceSet) {
    val cd2PojoSources = sourceSet.extensions.create(
      CD2PojoSourceDirectorySet::class.java, "cd2pojo",
      DefaultCD2PojoSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("cd2pojo", "${sourceSet.name} CD2Pojo sources"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    cd2PojoSources.srcDir(project.file("src/${sourceSet.name}/cd2pojo"))
    cd2PojoSources.filter.include("**/*.cd")
    cd2PojoSources.destinationDirectory.convention(
      project.layout.buildDirectory.dir("cd2pojo/${sourceSet.name}")
    )

    // Explicitly capture only a FileCollection for compatibility with configuration-cache.
    val cd2PojoSourceFiles = cd2PojoSources as FileCollection
    sourceSet.resources.filter.exclude(
      spec { element ->
        cd2PojoSourceFiles.contains(element.file)
      }
    )

    sourceSet.allSource.source(cd2PojoSources)
  }

  /**
   * Registers the task that compiles the class diagram sources of [sourceSet] to Java.
   *
   * The generated Java source directory is added to [SourceSet.getJava], and the task
   * is registered as the producer of the CD2Pojo source-set output.
   */
  private fun createCompileCD2PojoTask(
    sourceSet: SourceSet
  ): TaskProvider<CD2PojoCompile> = with (project) {

    val cd2PojoSources = sourceSet.extensions.getByType(
      CD2PojoSourceDirectorySet::class.java
    )

    val compile = tasks.register(sourceSet.compileCD2PojoTaskName, CD2PojoCompile::class.java)

    compile.configure {
      it.description = "Compile the class diagrams in source set ${sourceSet.name} to Java."

      it.modelpath.setFrom(cd2PojoSources.sourceDirectories)
      it.outputDir.set(cd2PojoSources.destinationDirectory)

      sourceSet.java.srcDir(it.javaOutputDir())
      it.hwcPath.setFrom(provider {
        sourceSet.allJava.sourceDirectories.files
          .filter { dir -> !dir.startsWith(layout.buildDirectory.get().asFile) }
      })
    }

    sourceSet.cd2PojoSourceDirectories.get().compiledBy(compile, CD2PojoCompile::outputDir)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(compile) }

    return compile
  }
}
