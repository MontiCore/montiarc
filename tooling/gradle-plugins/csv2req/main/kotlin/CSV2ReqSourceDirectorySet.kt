/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.csv2req

import java.util.Optional
import javax.inject.Inject
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.internal.tasks.TaskDependencyFactory
import org.gradle.api.tasks.SourceSet

/**
 * [SourceDirectorySet] for requirements CSV files, used for csv2req
 */
interface CSV2ReqSourceDirectorySet : SourceDirectorySet

abstract class DefaultCSV2ReqSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  taskDependencyFactory: TaskDependencyFactory
) : DefaultSourceDirectorySet(sourceDirectorySet, taskDependencyFactory), CSV2ReqSourceDirectorySet

val SourceSet.csv2req
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(CSV2ReqSourceDirectorySet::class.java)
  )

val SourceSet.compileCSV2ReqTaskName: String
  get() = getCompileTaskName("csv2req")
