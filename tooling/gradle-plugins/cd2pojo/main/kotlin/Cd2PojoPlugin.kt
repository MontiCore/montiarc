/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "cd2pojoGenerator"

const val CD2POJO_TOOL_CLASS = "de.monticore.cd2pojo.CD2PojoTool"

const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:cd2pojo:${VERSION}"

const val SE_LOGGING_PROJECT_REF = "de.se_rwth.commons:se-commons-logging:${VERSION}"

@Suppress("unused")
@Incubating
class Cd2PojoPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    this.project.extensions.extraProperties.set("CDTaskType", Cd2PojoCompile::class.java)

    with (project) {
      pluginManager.apply("java-base")

      addGeneratorDependency()

      getSourceSetsOf(project).all { sourceSet ->
        // Adding an entry for cd2pojo to all source sets and creating compile tasks from them
        addCd2PojoEntryToSourceSet(sourceSet)
        createCompileCd2PojoTask(sourceSet)
        dependencies.addProvider(sourceSet.implementationConfigurationName, provider { SE_LOGGING_PROJECT_REF })
      }

      pluginManager.apply(Cd2PojoDistributionPlugin::class.java)
    }
  }

  private fun addGeneratorDependency() = with (project) {
    // Add a configuration to store the classpath for executing the cd2pojo generator
    configurations.create(GENERATOR_DEPENDENCY_CONFIG_NAME) {
      it.isCanBeResolved = true  // Necessary so that gradle can actually find a jar artifact
      it.isCanBeConsumed = false  // We do not use this configuration for publishing
    }

    // Add a dependency on the cd2pojo jar
    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider { MAVEN_GENERATOR_PROJECT_REF })
  }

  private fun getSourceSetsOf(project: Project): SourceSetContainer {
    return project.extensions
      .getByType(JavaPluginExtension::class.java)
      .sourceSets
  }

  /**
   * Adds the entry "cd2pojo" to every source set where users can put class diagram models.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * cd2pojo sources is added to the java sources of the same SourceSet
   */
  private fun addCd2PojoEntryToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      Cd2PojoSourceDirectorySet::class.java, "cd2pojo",
      DefaultCd2PojoSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("cd2pojo", "${sourceSet.name} cd2pojo source"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("cd2pojo/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/cd2pojo"))
    srcDirSet.filter.include("**/*.cd")


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
    sourceSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
    sourceSet.allSource.source(srcDirSet)
  }

  /**
   * Create a task that compiles the class diagram sources of the specified source set.
   * Moreover, the [destinationDirectory][SourceDirectorySet.getDestinationDirectory] of the
   * task is added to the java sources of the same SourceSet.
   */
  private fun createCompileCd2PojoTask(sourceSet: SourceSet): TaskProvider<Cd2PojoCompile> = with (project) {
    val cdSrcDirSet = sourceSet.extensions.getByType(Cd2PojoSourceDirectorySet::class.java)
    val taskName = sourceSet.compileCd2PojoTaskName
    val generateTask = tasks.register(taskName, Cd2PojoCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the class diagram models in source set ${sourceSet.name}."

      genTask.modelPath.setFrom(cdSrcDirSet.sourceDirectories)
      genTask.outputDir.set(cdSrcDirSet.destinationDirectory)

      sourceSet.java.srcDir(genTask.javaOutputDir())
      genTask.hwcPath.setFrom(provider {
        sourceSet.allJava.sourceDirectories.files
          .filter { !it.startsWith(layout.buildDirectory.get().asFile) }
      })
    }

    sourceSet.cd2pojo.get().compiledBy(generateTask, Cd2PojoCompile::outputDir)
    tasks.named(sourceSet.compileJavaTaskName) { it.dependsOn(generateTask) }

    return generateTask
  }
}
