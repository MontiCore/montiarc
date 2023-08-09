/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet
import org.gradle.api.Project

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
