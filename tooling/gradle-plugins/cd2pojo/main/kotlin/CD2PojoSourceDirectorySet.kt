/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import java.util.Optional
import javax.inject.Inject
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet

/**
 * A [SourceDirectorySet] containing class-diagram sources processed by CD2Pojo.
 */
interface CD2PojoSourceDirectorySet : SourceDirectorySet

/**
 * Default implementation of [CD2PojoSourceDirectorySet].
 *
 * This delegates source-directory and task-dependency handling to Gradle's internal
 * [DefaultSourceDirectorySet] implementation.
 */
abstract class DefaultCD2PojoSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory),
  CD2PojoSourceDirectorySet

/**
 * Returns the CD2Pojo source directory set registered on this source set, if present.
 */
val SourceSet.cd2PojoSourceDirectories: Optional<CD2PojoSourceDirectorySet>
  get() = Optional.ofNullable(
    extensions.findByType(CD2PojoSourceDirectorySet::class.java)
  )

/**
 * Returns the name of the task compiling CD2Pojo sources for this source set.
 */
val SourceSet.compileCD2PojoTaskName: String
  get() = getCompileTaskName("cd2pojo")
