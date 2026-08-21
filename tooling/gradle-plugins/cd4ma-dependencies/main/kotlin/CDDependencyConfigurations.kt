/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

const val CD2POJO_4_MONTIARC_USAGE = "classdiagram-for-montiarc-api"

/**
 * Name of the configuration used to declare class diagram dependencies of MontiArc models.
 *
 * For example, this is `cd2pojo4montiarc` for `main` and `testCd2pojo4montiarc` for `test`.
 */
val SourceSet.cd2pojo4montiarcConfigName: String
  get() = nameCD4MAConfig()

/**
 * Name of the resolvable configuration containing the cd symbols required while compiling the
 * MontiArc models that declare a dependency via `cd2pojo4montiarc`.
 *
 * For example, this is `cd2pojo4montiarcSymbolpath` for `main` and
 * `testCd2pojo4montiarcSymbolpath` for `test`.
 */
val SourceSet.cd2pojo4montiarcSymbolpathConfigName: String
  get() = nameCD4MAConfig("Symbolpath")

/**
 * Name of the consumable configuration exposing the transitive cd dependencies of the MontiArc
 * models.
 *
 * For example, this is `cd2pojo4montiarcApiElements` for `main` and
 * `testCd2pojo4montiarcApiElements` for `test`.
 */
val SourceSet.cd2pojo4montiarcApiElementsConfigName: String
  get() = nameCD4MAConfig("ApiElements")

private fun SourceSet.nameCD4MAConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "cd2pojo4montiarc$suffix" else "${name}Cd2pojo4montiarc$suffix"

/**
 * The publication metadata for the set of transitive cd dependencies that the MontiArc models have.
 * (Most notably, the [Usage] attribute value is [CD2POJO_4_MONTIARC_USAGE]
 */
fun addCD4MAJarAttributesTo(config: Configuration, project: Project) = with(project) {
  config.attributes {
    it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, CD2POJO_4_MONTIARC_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
  }
}
