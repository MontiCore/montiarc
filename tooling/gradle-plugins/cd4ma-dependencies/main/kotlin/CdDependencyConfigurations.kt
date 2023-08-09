/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.artifacts.Configuration
import org.gradle.api.Project
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

const val CD2POJO_4_MONTIARC_USAGE = "cd2pojo-for-montiarc-api"

val SourceSet.cd2pojo4MaDeclarationConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarc"
    } else {
      "${this.name}Cd2pojo4montiarc"
    }

val SourceSet.cd2Pojo4MaSymbolDependencyConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarcSymbolDependencies"
    } else {
      "${this.name}Cd2pojo4montiarcSymbolDependencies"
    }


val SourceSet.outgoingCd4MaDependenciesConfigName
  get() =
    if (SourceSet.isMain(this)) {
      "cd2pojo4montiarcSymbolDependencyElements"
    } else {
      "${this.name}Cd2pojo4montiarcSymbolDependencyElements"
    }

/**
 * The publication metadata for the set of transitive cd dependencies that the MontiArc models have.
 * (Most notably, the [Usage] attribute value is [CD2POJO_4_MONTIARC_USAGE]
 */
fun addCd4maJarAttributesTo(config: Configuration, project: Project) = with(project) {
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
