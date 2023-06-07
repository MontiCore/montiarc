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

val SourceSet.montiarcDependencyDeclarationConfigName: String
  get() = if (SourceSet.isMain(this)) {
      "montiarc"
    } else {
      "${this.name}Montiarc"
    }

val SourceSet.montiarcSymbolDependencyConfigurationName: String
  get() = if (SourceSet.isMain(this)) {
      "montiarcSymbolDependencies"
    } else {
      "${this.name}MontiarcSymbolDependencies"
    }

val SourceSet.montiarcOutgoingSymbolsConfigurationName: String
  get() = if (SourceSet.isMain(this)) {
      "montiarcSymbolElements"
    } else {
      "${this.name}MontiarcSymbolElements"
    }

val SourceSet.compileMontiarcTaskName: String
  get() = getCompileTaskName("montiarc")

val SourceSet.montiarcSymbolsJarTaskName: String
  get() = getTaskName(null, "montiarcSymbolsJar")

val SourceSet.sourcesJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
      MONTIARC_SOURCES_BASE_CLASSIFIER
    } else {
      "${this.name}-$MONTIARC_SOURCES_BASE_CLASSIFIER"
    }

val SourceSet.symbolsJarClassifierName: String
  get() =  if (SourceSet.isMain(this)) {
      MONTIARC_SYMBOLS_BASE_CLASSIFIER
    } else {
      "${this.name}-$MONTIARC_SYMBOLS_BASE_CLASSIFIER"
    }

val SourceSet.montiarc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(MontiarcSourceDirectorySet::class.java)
  )
