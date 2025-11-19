/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.SourceSet
import java.util.Optional
import javax.inject.Inject
import org.gradle.api.internal.tasks.TaskDependencyFactory

/**
 * [SourceDirectorySet] for class diagram sources, used for sd2arc
 */
interface Sd2ArcSourceDirectorySet : SourceDirectorySet

abstract class DefaultSd2ArcSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), Sd2ArcSourceDirectorySet

val SourceSet.sd2arc
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(Sd2ArcSourceDirectorySet::class.java)
  )


val SourceSet.compileSd2ArcTaskName: String
  get() = getCompileTaskName("sd2arc")
