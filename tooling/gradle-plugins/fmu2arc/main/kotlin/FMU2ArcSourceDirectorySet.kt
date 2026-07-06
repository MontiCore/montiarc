/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

import java.util.Optional
import javax.inject.Inject
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet

/**
 * [SourceDirectorySet] for fmu sources, used for fmu2arc
 */
interface FMU2ArcSourceDirectorySet : SourceDirectorySet

abstract class DefaultFMU2ArcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), FMU2ArcSourceDirectorySet

val SourceSet.fmu2arc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(FMU2ArcSourceDirectorySet::class.java)
  )


val SourceSet.compileFMU2ArcTaskName: String
  get() = getCompileTaskName("fmu2arc")
