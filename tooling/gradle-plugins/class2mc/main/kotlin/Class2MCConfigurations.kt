/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.class2mc

import org.gradle.api.Project
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

/**
 * Usage value used for the generic class2mc dependency exchange between projects/plugins.
 */
const val CLASS2MC_USAGE = "class2mc-api"

val SourceSet.class2mcConfigName get() = nameClass2MCConfig()
val SourceSet.class2mcClasspathConfigName get() = nameClass2MCConfig("Classpath")
val SourceSet.class2mcApiElementsConfigName get() = nameClass2MCConfig("ApiElements")

private fun SourceSet.nameClass2MCConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "class2mc$suffix" else "${name}Class2mc$suffix"

/**
 * Shared attribute set for both the classpath and the apiElements variant.
 */
fun attachClass2MCAttributes(attrs: AttributeContainer,
                             project: Project,
                             libraryElements: String) {
  val objects = project.objects
  attrs.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage::class.java, CLASS2MC_USAGE))
  attrs.attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, Category.LIBRARY))
  attrs.attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling::class.java, Bundling.EXTERNAL))
  attrs.attribute(
    LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
    objects.named(LibraryElements::class.java, libraryElements)
  )
}
