/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.cd2pojo.plugin

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.SourceSet
import java.util.*
import javax.inject.Inject

/**
 * [SourceDirectorySet] for class diagram sources, used for cd2pojo
 */
interface Cd2PojoSourceDirectorySet : SourceDirectorySet

abstract class DefaultCd2PojoSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  // taskDependencyFactory: TaskDependencyFactory  // Needed starting with gradle v.8 */
) : DefaultSourceDirectorySet(sourceDirectorySet), Cd2PojoSourceDirectorySet

fun SourceSet.cd2pojo(): Optional<SourceDirectorySet> {
  return Optional.ofNullable(
    extensions.findByType(Cd2PojoSourceDirectorySet::class.java)
  )
}

fun SourceSet.getCompileCd2PojoTaskName(): String = getCompileTaskName("cd2pojo")