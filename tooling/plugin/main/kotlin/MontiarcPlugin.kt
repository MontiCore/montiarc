/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.tasks.Jar


const val GENERATOR_DEPENDENCY_CONFIG_NAME = "maGenerator"
const val MONTIARC_CONFIG_NAME = "montiarc" // TODO: refactor this to include the sourceSetName (if it's not "main") SourceSet.MAIN_SOURCESET_NAM
const val UNPACK_MA_DEPENDENCIES_TASK_NAME = "unpackMontiarcDependencies"  // TODO: refactor this to include the sourceSetName (if it's not "main") SourceSet.MAIN_SOURCESET_NAM

const val DSL_EXTENSION_NAME = "montiarc"
const val MA_TOOL_CLASS = "montiarc.generator.MontiArcTool"
const val INTERNAL_GENERATOR_PROJECT_REF = ":generators:ma2java"
const val MAVEN_GENERATOR_PROJECT_REF = "montiarc.generators:ma2java"

/**
 * Adds a sourceset entry for Montiarc models and tasks that generate java code from Montiarc models.
 * @see MontiarcCompile
 */
class MontiarcPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    addJavaBasePlugin(project)

    val extension = project.extensions.create(DSL_EXTENSION_NAME, MAExtension::class.java)
    addGeneratorDependency(project, extension)

    addMontiarcToSourceSets(project, extension)

    /* TODO: later add dependency mechanism
    project.configurations.maybeCreate(MONTIARC_CONFIG_NAME)
    configureJarTask(project, extension)
    addUnpackModelsTask(project)
     */
  }

  private fun addJavaBasePlugin(project: Project) {
    if (!project.pluginManager.hasPlugin("java-base")) {
      project.pluginManager.apply("java-base")
    }
  }

  private fun addGeneratorDependency(project: Project, extension: MAExtension) {
    // Add a configuration to store the classpath for executing the ma2java generator
    project.configurations.maybeCreate(GENERATOR_DEPENDENCY_CONFIG_NAME)

    // Add a dependency on the ma2java jar. Depending on what the user wishes, ma2java may be drawn from maven
    // (default), or it may be an internal project dependency. This only makes sense for us, the MontiArc
    // developers, because this way we can directly test the freshly compiled version of ma2java.
    project.dependencies.addProvider(GENERATOR_DEPENDENCY_CONFIG_NAME, project.provider {
      if(extension.internalMontiArcTesting.get()) {
        project.project(INTERNAL_GENERATOR_PROJECT_REF)
      } else {
        "${MAVEN_GENERATOR_PROJECT_REF}:${GENERATOR_VERSION}"
      }
    })
  }

  private fun addMontiarcToSourceSets(project: Project, extension: MAExtension) {
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.all { srcSet ->

      val srcDirSet = srcSet.extensions.create(
        MontiarcSourceDirectorySet::class.java, "montiarc",
        DefaultMontiarcSourceDirectorySet::class.java,
        project.objects.sourceDirectorySet("montiarc", "${srcSet.name} montiarc source"),
        DefaultTaskDependencyFactory.withNoAssociatedProject()
      )

      val genDir = project.layout.buildDirectory.dir("montiarc/${srcSet.name}")
      srcDirSet.destinationDirectory.convention(genDir)
      srcDirSet.srcDir(project.file("src/${srcSet.name}/montiarc"))
      srcDirSet.filter.include("**/*.arc")


      // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
      // configuration cache.
      // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
      val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
      srcSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
      srcSet.allSource.source(srcDirSet)
      srcSet.java.srcDir(srcDirSet.destinationDirectory)

      // TODO: compiledBy, classesDirectory. Or not?
      createCompileMontiarcTask(project, srcSet, extension)
    }
  }

  private fun createCompileMontiarcTask(project: Project, sourceSet: SourceSet, extension: MAExtension) {
    val montiarcSrcDirSet = sourceSet.extensions.getByType(MontiarcSourceDirectorySet::class.java)
    val taskName = sourceSet.getTaskName("compile", "Montiarc")
    val generateTask = project.tasks.register(taskName, MontiarcCompile::class.java)

    generateTask.configure { genTask ->
      genTask.modelPath.setFrom(montiarcSrcDirSet.sourceDirectories)
      genTask.outputDir.set(montiarcSrcDirSet.destinationDirectory)
      genTask.symbolImportDir.setFrom(project.file("${project.projectDir}/src/${sourceSet.name}/symbols"))

      genTask.hwcPath.setFrom(project.provider {
        sourceSet.allJava.sourceDirectories.files
        .filter { !it.startsWith(montiarcSrcDirSet.destinationDirectory.asFile.get())}
      })
    }

    setTaskOrderAfterMaGenerator(generateTask, project, extension)
    project.tasks.named(sourceSet.compileJavaTaskName).configure { it.dependsOn(generateTask) }
  }

  private fun setTaskOrderAfterMaGenerator(generateTask: TaskProvider<MontiarcCompile>,
                                           project: Project,
                                           config: MAExtension) {
    generateTask.configure { genTask ->
      if (config.internalMontiArcTesting.get()) {
        genTask.mustRunAfter(project.project(INTERNAL_GENERATOR_PROJECT_REF).tasks.withType(Test::class.java))
      }
    }
  }

  private fun configureJarTask(project: Project, extension: MAExtension) {
    // TODO: make it possible to configure jar tasks of other source sets
    project.tasks.named("jar", Jar::class.java).configure { jar ->
      jar.from(extension.modelPath) { copy ->  // TODO: do not use the modelpath on which transitive montiarc dependencies lay. We should use the montiarc part of a sourceset. (sourceset support is a future)
        copy.include("**/*.arc")
      }
    }
  }

  private fun addUnpackModelsTask(project: Project) {
    project.tasks.register(UNPACK_MA_DEPENDENCIES_TASK_NAME, Copy::class.java) {copy -> // TODO: enable other source set directories
      copy.description = "Extracts model .sym files from jars so that they can be used." +
          "(And currently, we also extract .arc files, but this will be removed in the future.)"
      copy.dependsOn(project.configurations.named(MONTIARC_CONFIG_NAME))

      copy.into("${project.buildDir}/unpacked-montiarc-dependencies/main") // TODO: enable other source set directories

      project.configurations.named(MONTIARC_CONFIG_NAME).get().files.forEach {
        copy.from(project.zipTree(it)) {copySpec ->
          copySpec.include("**/*.arc")
          copySpec.include("**/*.sym")
          copySpec.includeEmptyDirs = false
          copySpec.eachFile {file ->
            // Add the name of the jar where files come from to the file path
            file.relativePath = file.relativePath.prepend(it.nameWithoutExtension)
          }
        }
      }
    }
  }


  /*
  private fun addTaskDependencies(project: Project) {
    // compileMontiarc dependsOn unpackMontiarcDependencies

    project.tasks.named(COMPILE_TASK_NAME).configure {
      t -> t.dependsOn(project.tasks.named(UNPACK_MA_DEPENDENCIES_TASK_NAME))
    }
  }
   */
}

