/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.cd2pojo

import org.gradle.api.Project
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Bundling
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.tasks.SourceSet

/**
 * Usage value identifying variants that contain class diagram symbol artifacts.
 */
const val CD2POJO_API_SYMBOL_USAGE = "cd2pojo-api"

/**
 * Base classifier for JARs containing compiled class diagram symbols.
 */
const val CD2POJO_SYMBOLS_BASE_CLASSIFIER = "cd2pojoSymbols"

/**
 * Name of the configuration used to declare dependencies on CD2Pojo model projects.
 *
 * For example, this is `cd2pojo` for `main` and `testCd2pojo` for `test`.
 */
val SourceSet.cd2pojoConfigName: String
  get() = nameCD2PojoConfig()

/**
 * Name of the resolvable configuration containing the library symbols
 * required while compiling the class diagrams source models to Java code.
 *
 * For example, this is `cd2pojoSymbolpath` for `main` and
 * `testCd2pojoSymbolpath` for `test`.
 */
val SourceSet.cd2pojoSymbolpathConfigName: String
  get() = nameCD2PojoConfig("Symbolpath")

/**
 * Name of the consumable configuration exposing compiled class diagram symbols.
 *
 * For example, this is `cd2pojoApiElements` for `main` and
 * `testCd2pojoApiElements` for `test`.
 */
val SourceSet.cd2pojoApiElementsConfigName: String
  get() = nameCD2PojoConfig("ApiElements")

/**
 * Name of the task that packages compiled class diagram symbols into a JAR.
 */
val SourceSet.cd2pojoSymbolsJarTaskName: String
  get() = getTaskName("cd2Pojo", "symbolsJar")

/**
 * Archive classifier for the JAR containing class diagram symbols.
 */
val SourceSet.cd2pojoSymbolsJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
    CD2POJO_SYMBOLS_BASE_CLASSIFIER
  } else {
    "$name-$CD2POJO_SYMBOLS_BASE_CLASSIFIER"
  }

private fun SourceSet.nameCD2PojoConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "cd2pojo$suffix" else "${name}Cd2pojo$suffix"

/**
 * Adds attributes for selecting or exposing a JAR variant containing class diagram symbols.
 */
fun attachCD2PojoSymbolAttributes(
  attributes: AttributeContainer,
  project: Project,
  libraryElements: String = LibraryElements.JAR
) {
  val objects = project.objects

  attributes.attribute(
    Usage.USAGE_ATTRIBUTE,
    objects.named(Usage::class.java, CD2POJO_API_SYMBOL_USAGE)
  )
  attributes.attribute(
    Category.CATEGORY_ATTRIBUTE,
    objects.named(Category::class.java, Category.LIBRARY)
  )
  attributes.attribute(
    Bundling.BUNDLING_ATTRIBUTE,
    objects.named(Bundling::class.java, Bundling.EXTERNAL)
  )
  attributes.attribute(
    LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
    objects.named(LibraryElements::class.java, libraryElements)
  )
}
