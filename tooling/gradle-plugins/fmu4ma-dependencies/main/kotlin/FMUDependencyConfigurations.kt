/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import montiarc.gradle.fmu2arc.FMU2ARC_API_SYMBOL_USAGE
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

const val FMU2ARC_4_MONTIARC_USAGE = "fmu2arc-for-montiarc-api"

/**
 * Name of the configuration used to declare fmu dependencies of MontiArc models.
 *
 * For example, this is `fmu2arc4montiarc` for `main` and `testFMU2arc4montiarc` for `test`.
 */
val SourceSet.fmu2arc4montiarcConfigName: String
  get() = nameFMU4MAConfig()

/**
 * Name of the resolvable configuration containing the fmu files required for
 * executing montiarc components declared via `fmu2arc4montiarc`.
 *
 * For example, this is `fmu2arc4montiarcRuntime` for `main` and
 * `testFMU2arc4montiarcRuntime` for `test`.
 */
val SourceSet.fmu2arc4montiarcRuntimeConfigName: String
  get() = nameFMU4MAConfig("Runtime")

/**
 * Name of the resolvable configuration containing the fmu symbols required while compiling the
 * MontiArc models that declare a dependency via `fmu2arc4montiarc`.
 *
 * For example, this is `fmu2arc4montiarcSymbolpath` for `main` and
 * `testFMU2arc4montiarcSymbolpath` for `test`.
 */
val SourceSet.fmu2arc4montiarcSymbolpathConfigName: String
  get() = nameFMU4MAConfig("Symbolpath")

/**
 * Name of the resolvable configuration containing the montiarc component symbols published by
 * fmu2arc-declared projects, so consumers of `fmu2arc4montiarc(...)` can import generated wrapper
 * components directly.
 *
 * For example, this is `fmu2arc4montiarcCompSymbolpath` for `main` and
 * `testFMU2arc4montiarcCompSymbolpath` for `test`.
 */
val SourceSet.fmu2arc4montiarcCompSymbolpathConfigName: String
  get() = nameFMU4MAConfig("CompSymbolpath")

/**
 * Name of the consumable configuration exposing the transitive fmu dependencies of the MontiArc
 * models.
 *
 * For example, this is `fmu2arc4montiarcApiElements` for `main` and
 * `testFMU2arc4montiarcApiElements` for `test`.
 */
val SourceSet.fmu2arc4montiarcApiElementsConfigName: String
  get() = nameFMU4MAConfig("ApiElements")

private fun SourceSet.nameFMU4MAConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "fmu2arc4montiarc$suffix" else "${name}FMU2arc4montiarc$suffix"

/**
 * The publication metadata for the set of transitive dependencies that the MontiArc models have.
 * (Most notably, the [Usage] attribute value is [FMU2ARC_4_MONTIARC_USAGE]
 */
fun addFMU4MAJarAttributesTo(config: Configuration, project: Project) = with(project) {
  config.attributes {
    it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, FMU2ARC_4_MONTIARC_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
  }
}

/**
 * The publication metadata for the set of transitive dependencies that the MontiArc models have.
 * (Most notably, the [Usage] attribute value is [FMU2ARC_API_SYMBOL_USAGE]
 */
fun addFMUSymbolAttributesTo(config: Configuration, project: Project) = with(project) {
  config.attributes {
    it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, FMU2ARC_API_SYMBOL_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
  }
}

