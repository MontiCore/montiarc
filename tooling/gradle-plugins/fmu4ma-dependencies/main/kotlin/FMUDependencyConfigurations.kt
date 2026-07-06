/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

const val FMU2ARC_4_MONTIARC_USAGE = "fmu2arc-for-montiarc-api"

const val FMU2ARC_SYMBOL_USAGE = "fmu2arcSymbol-for-montiarc-api"

val SourceSet.fmu2arc4MaDeclarationConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "fmu2arc4montiarc"
    } else {
      "${this.name}FMU2arc4montiarc"
    }

val SourceSet.fmu2arc4MaFileDependencyConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "fmu2arc4montiarcFileDependencies"
    } else {
      "${this.name}FMU2arc4montiarcFileDependencies"
    }

val SourceSet.fmu2arc4MaSymbolDependencyConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "fmu2arc4montiarcSymbolDependencies"
    } else {
      "${this.name}FMU2arc4montiarcSymbolDependencies"
    }

val SourceSet.outgoingFMU4MaDependenciesConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "fmu2arc4montiarcDependencyElements"
    } else {
      "${this.name}FMU2arc4montiarcDependencyElements"
    }


// This config is used to make Symbols of Consumers of FMUs accessible without
// having to declare a montiarc(project(...)) dependency
val SourceSet.fmu2arc4MaCompSymbolDependencyConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "fmu2arc4montiarcCompSymbolDependencies"
    } else {
      "${this.name}FMU2arc4montiarcCompSymbolDependencies"
    }


/**
 * The publication metadata for the set of transitive dependencies that the MontiArc models have.
 * (Most notably, the [Usage] attribute value is [FMU2ARC_4_MONTIARC_USAGE]
 */
fun addFMU4maJarAttributesTo(config: Configuration, project: Project) = with(project) {
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
 * (Most notably, the [Usage] attribute value is [FMU2ARC_SYMBOL_USAGE]
 */
fun addFMUSymbolAttributesTo(config: Configuration, project: Project) = with(project) {
  config.attributes {
    it.attribute(Category.CATEGORY_ATTRIBUTE, project.objects.named(Category::class.java, Category.LIBRARY))
    it.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, FMU2ARC_SYMBOL_USAGE))
    it.attribute(Bundling.BUNDLING_ATTRIBUTE, project.objects.named(Bundling::class.java, Bundling.EXTERNAL))
    it.attribute(
      LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
      project.objects.named(LibraryElements::class.java, LibraryElements.JAR)
    )
  }
}

