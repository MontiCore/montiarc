/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.SourceSet
import java.util.Optional
import javax.inject.Inject

/**
 * [SourceDirectorySet] for Montiarc sources
 */
interface MontiarcSourceDirectorySet : SourceDirectorySet

abstract class DefaultMontiarcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  // taskDependencyFactory: TaskDependencyFactory  // Needed starting with gradle v.8 */
) : DefaultSourceDirectorySet(sourceDirectorySet), MontiarcSourceDirectorySet

fun SourceSet.montiarcDependencyConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarc"
  } else {
    "${this.name}Montiarc"
  }
}

fun SourceSet.montiarcSymbolDependencyConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarcSymbolDependencies"
  } else {
    "${this.name}MontiarcSymbolDependencies"
  }
}

fun SourceSet.montiarcOutgoingSymbolsConfigurationName(): String {
  return if (SourceSet.isMain(this)) {
    "montiarcSymbolElements"
  } else {
    "${this.name}MontiarcSymbolElements"
  }
}

fun SourceSet.getCompileMontiarcTaskName(): String {
  return getCompileTaskName("montiarc")
}

fun SourceSet.getMontiarcSymbolsJarTaskName(): String {
  return getTaskName(null, "montiarcSymbolsJar")
}

fun SourceSet.getSourcesJarClassifierName(): String {
  return if (SourceSet.isMain(this)) {
    MONTIARC_SOURCES_BASE_CLASSIFIER
  } else {
    "${this.name}-$MONTIARC_SOURCES_BASE_CLASSIFIER"
  }
}

fun SourceSet.getSymbolsJarClassifierName(): String {
  return if (SourceSet.isMain(this)) {
    MONTIARC_SYMBOLS_BASE_CLASSIFIER
  } else {
    "${this.name}-$MONTIARC_SYMBOLS_BASE_CLASSIFIER"
  }
}

fun SourceSet.montiarc(): Optional<SourceDirectorySet> {
  return Optional.ofNullable(
    extensions.findByType(MontiarcSourceDirectorySet::class.java)
  )
}