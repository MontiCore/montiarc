/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import java.util.Optional
import javax.inject.Inject
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet

/**
 * [SourceDirectorySet] for MontiArc sources
 */
interface MontiArcSourceDirectorySet : SourceDirectorySet

abstract class DefaultMontiArcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), MontiArcSourceDirectorySet

val SourceSet.montiarc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(MontiArcSourceDirectorySet::class.java)
  )
