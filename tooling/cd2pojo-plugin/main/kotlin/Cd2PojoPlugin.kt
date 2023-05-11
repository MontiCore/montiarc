/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.cd2pojo.plugin

import org.gradle.api.Incubating
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

const val GENERATOR_DEPENDENCY_CONFIG_NAME = "cd2pojoGenerator"
const val DSL_EXTENSION_NAME = "cd2pojo"

const val CD2POJO_TOOL_CLASS = "de.monticore.cd2pojo.CD2PojoTool"

const val INTERNAL_GENERATOR_PROJECT_REF = ":generators:cd2pojo"
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:cd2pojo"

@Suppress("unused")
@Incubating
class Cd2PojoPlugin : Plugin<Project> {

  private lateinit var project: Project
  private lateinit var cdExtension: Cd2PojoExtension

  override fun apply(project: Project) {
    this.project = project
    this.cdExtension = project.extensions.create(DSL_EXTENSION_NAME, Cd2PojoExtension::class.java)

    with (project) {
      pluginManager.apply("java-base")

      addGeneratorDependency()

      getSourceSetsOf(project).all { sourceSet ->
        // Adding an entry for cd2pojo to all source sets and creating compile tasks from them
        addCd2PojoEntryToSourceSet(sourceSet)
        createCompileCd2PojoTask(sourceSet)
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

    // Add a dependency on the cd2pojo jar. Depending on what the user wishes, cd2pojo may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of cd2pojo.
    dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, provider {
      if(cdExtension.internalMontiArcTesting.get()) {
        project(INTERNAL_GENERATOR_PROJECT_REF)
      } else {
        "${MAVEN_GENERATOR_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
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
      // DefaultTaskDependencyFactory.withNoAssociatedProject()  // Needed starting with Gradle v.8
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
    val taskName = sourceSet.getCompileCd2PojoTaskName()
    val generateTask = tasks.register(taskName, Cd2PojoCompile::class.java)

    generateTask.configure { genTask ->
      genTask.description = "Generates java code from the class diagram models in source set ${sourceSet.name}."

      genTask.modelPath.setFrom(cdSrcDirSet.sourceDirectories)
      genTask.outputDir.set(cdSrcDirSet.destinationDirectory)

      sourceSet.java.srcDir(genTask.javaOutputDir())
      genTask.hwcPath.setFrom(provider {
        sourceSet.allJava.sourceDirectories.files
          .filter { !it.startsWith(buildDir)}
      })
    }

    sourceSet.cd2pojo().get().compiledBy(generateTask, Cd2PojoCompile::outputDir)
    setTaskOrderAfterCdGenerator(generateTask)
    tasks.named(sourceSet.compileJavaTaskName).configure { it.dependsOn(generateTask) }

    return generateTask
  }

  /**
   * If [Cd2PojoExtension.internalMontiArcTesting] is true, then this method schedules the [Cd2PojoCompile] task to run
   * after the tests of the generator finished.
   */
  private fun setTaskOrderAfterCdGenerator(generateTask: TaskProvider<Cd2PojoCompile>) = with (project) {
    generateTask.configure { genTask ->
      if (cdExtension.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project(INTERNAL_GENERATOR_PROJECT_REF).tasks.withType(Test::class.java))
      }
    }
  }
}