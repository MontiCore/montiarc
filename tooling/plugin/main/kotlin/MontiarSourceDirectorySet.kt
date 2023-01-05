/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet
import java.util.Optional
import javax.inject.Inject

/**
 * [SourceDirectorySet] for Montiarc sources
 */
interface MontiarcSourceDirectorySet : SourceDirectorySet

abstract class DefaultMontiarcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory  // Needed starting with gradle v.8?
) : DefaultSourceDirectorySet(sourceDirectorySet), MontiarcSourceDirectorySet

fun SourceSet.montiarcDependencyConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarc"
  } else {
    "${this.name}Montiarc"
  }
}

fun SourceSet.montiarcSourceDependencyConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarcSourceDependencies"
  } else {
    "${this.name}MontiarcSourceDependencies"
  }
}

fun SourceSet.montiarcOutgoingSourcesConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarcSourcesElements"
  } else {
    "${this.name}MontiarcSourcesElements"
  }
}

fun SourceSet.getCompileMontiarcTaskName(): String {
  return getCompileTaskName("montiarc")
}

fun SourceSet.getUnpackMontiarcDependenciesTaskName(): String {
  return getTaskName("unpack", "MontiarcDependencies")
}

fun SourceSet.getMontiarcSourcesJarTaskName(): String {
  return getTaskName(null, "montiarcSourcesJar")
}

fun SourceSet.getSourcesJarClassifierName(): String {
  return if (SourceSet.isMain(this)) {
    MONTIARC_SOURCES_BASE_CLASSIFIER
  } else {
    "${this.name}-$MONTIARC_SOURCES_BASE_CLASSIFIER"
  }
}

fun SourceSet.montiarc(): Optional<SourceDirectorySet> {
  return Optional.ofNullable(
    extensions.findByType(MontiarcSourceDirectorySet::class.java)
  )
}