/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.lambdas.SerializableLambdas
import org.gradle.api.internal.tasks.DefaultTaskDependencyFactory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Adds a sourceset entry for MontiArc models to every [SourceSet].
 * Usage:
 * ```
 * sourceSets {
 *   main {
 *     montiarc.srcDir("path/to/montiarc/models")
 *     // Default is src/main/montiarc for main
 *     // For other source sets, the "main" part of the path is replace by the source set name
 *   }
 * }
 * ```
 */
@Suppress("unused")
class MontiArcSourcesPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with(project) {
      pluginManager.apply("java-base")
      sourceSetsOf(project).all { addMontiArcEntryToSourceSet(it) }
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer =
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

  /**
   * Adds the entry "montiarc" to every source set where users can put montiarc models.
   */
  private fun addMontiArcEntryToSourceSet(sourceSet: SourceSet) {
    val srcDirSet = sourceSet.extensions.create(
      MontiArcSourceDirectorySet::class.java, "montiarc",
      DefaultMontiArcSourceDirectorySet::class.java,
      project.objects.sourceDirectorySet("montiarc", "${sourceSet.name} montiarc source"),
      DefaultTaskDependencyFactory.withNoAssociatedProject()
    )

    // Setting default values for the SourceDirectorySet
    val destinationDir = project.layout.buildDirectory.dir("montiarc/${sourceSet.name}")
    srcDirSet.destinationDirectory.convention(destinationDir)
    srcDirSet.srcDir(project.file("src/${sourceSet.name}/montiarc"))
    srcDirSet.filter.include("**/*.arc")


    // Casting the SrcDirSet to a FileCollection seems to be necessary due to compatibility reasons with the
    // configuration cache.
    // See https://github.com/gradle/gradle/blob/d36380f26658d5cf0bf1bfb3180b9eee6d1b65a5/subprojects/scala/src/main/java/org/gradle/api/plugins/scala/ScalaBasePlugin.java#L194
    val srcDirectorySetAsFileCollection = srcDirSet as FileCollection
    sourceSet.resources.exclude(SerializableLambdas.spec { el -> srcDirectorySetAsFileCollection.contains(el.file) })
    sourceSet.allSource.source(srcDirSet)
  }
}
