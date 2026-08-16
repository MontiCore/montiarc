/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

const val MONTIARC_API_SYMBOL_USAGE = "montiarc-api"

val SourceSet.montiarcDependencyDeclarationConfigName: String
  get() = if (SourceSet.isMain(this)) {
    "montiarc"
  } else {
    "${this.name}Montiarc"
  }

val SourceSet.montiarcSymbolDependencyConfigurationName: String
  get() = if (SourceSet.isMain(this)) {
    "montiarcSymbolDependencies"
  } else {
    "${this.name}MontiarcSymbolDependencies"
  }

val SourceSet.montiarcOutgoingSymbolsConfigurationName: String
  get() = if (SourceSet.isMain(this)) {
    "montiarcSymbolElements"
  } else {
    "${this.name}MontiarcSymbolElements"
  }

/**
 * Name of the configuration used to declare dependencies on MontiArc model projects.
 *
 * For example, this is `montiarc` for `main` and `testMontiarc` for `test`.
 */
val SourceSet.montiarcConfigName: String
  get() = nameMontiarcConfig()

/**
 * Name of the resolvable configuration containing the model symbols required while compiling
 * MontiArc source models that depend on other MontiArc models.
 *
 * For example, this is `montiarcSymbolpath` for `main` and `testMontiarcSymbolpath` for `test`.
 */
val SourceSet.montiarcSymbolpathConfigName: String
  get() = nameMontiarcConfig("Symbolpath")

/**
 * Name of the consumable configuration exposing compiled MontiArc symbols.
 *
 * For example, this is `montiarcApiElements` for `main` and `testMontiarcApiElements` for `test`.
 */
val SourceSet.montiarcApiElementsConfigName: String
  get() = nameMontiarcConfig("ApiElements")

private fun SourceSet.nameMontiarcConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "montiarc$suffix" else "${name}Montiarc$suffix"

/**
 * Adds the gradle module attributes to the configuration that mark it as a jar that contains .arcsym models.
 */
fun addMontiarcSymbolJarAttributesTo(config: Configuration, project: Project) = with (project) {
  config.attributes {
    it.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, MONTIARC_API_SYMBOL_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
  }
}
