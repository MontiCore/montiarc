/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import javax.inject.Inject

/**
 * [SourceDirectorySet] for Montiarc sources
 */
interface MontiarcSourceDirectorySet : SourceDirectorySet

abstract class DefaultMontiarcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory  // Needed starting with gradle v.8?
) : DefaultSourceDirectorySet(sourceDirectorySet), MontiarcSourceDirectorySet