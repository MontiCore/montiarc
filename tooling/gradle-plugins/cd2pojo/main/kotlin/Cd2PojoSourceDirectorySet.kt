/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.api.tasks.SourceSet
import java.util.Optional
import javax.inject.Inject

/**
 * [SourceDirectorySet] for class diagram sources, used for cd2pojo
 */
interface Cd2PojoSourceDirectorySet : SourceDirectorySet

abstract class DefaultCd2PojoSourceDirectorySet @Inject constructor(
  sourceDirectorySet: SourceDirectorySet,
  // taskDependencyFactory: TaskDependencyFactory  // Needed starting with gradle v.8 */
) : DefaultSourceDirectorySet(sourceDirectorySet), Cd2PojoSourceDirectorySet

val SourceSet.cd2pojo
  get(): Optional<SourceDirectorySet> = Optional.ofNullable(
    extensions.findByType(Cd2PojoSourceDirectorySet::class.java)
  )


val SourceSet.compileCd2PojoTaskName: String
  get() = getCompileTaskName("cd2pojo")
