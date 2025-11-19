/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import java.util.Optional
import javax.inject.Inject
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet

/**
 * [SourceDirectorySet] for Montiarc sources
 */
interface MontiarcSourceDirectorySet : SourceDirectorySet

abstract class DefaultMontiarcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), MontiarcSourceDirectorySet

val SourceSet.montiarc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(MontiarcSourceDirectorySet::class.java)
  )
