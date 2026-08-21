/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet
import java.util.Optional
import javax.inject.Inject

/**
 * [SourceDirectorySet] for class diagram sources, used for sd2arc
 */
interface SD2ArcSourceDirectorySet : SourceDirectorySet

abstract class DefaultSD2ArcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), SD2ArcSourceDirectorySet

val SourceSet.sd2arc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(SD2ArcSourceDirectorySet::class.java)
  )


val SourceSet.compileSD2ArcTaskName: String
  get() = getCompileTaskName("sd2arc")
