/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.class2mc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

/**
 * Creates a generic dependency bucket for classes that shall be usable
 * from *any* MontiCore-based modeling language (class diagrams, state
 * machines, MontiArc components, ...).
 *
 * ```
 * dependencies {
 *   class2mc("some.other:project.foo:2.0.0")
 *   testClass2mc("some.other:project.bar:2.0.0")
 * }
 * ```
 *
 * Language-specific plugins that need these classes (e.g. a CD4Analysis
 * or a MontiArc plugin) extend their own resolving configuration from
 * [SourceSet.class2mcClasspathConfigName] and, if they publish generated
 * code themselves, extend their outgoing variant from
 * [SourceSet.class2mcConfigName] to forward the dependencies transitively.
 */
@Suppress("unused")
class Class2MCPlugin : Plugin<Project> {

  private lateinit var project: Project

  override fun apply(project: Project) {
    this.project = project

    with(project) {
      pluginManager.apply(JavaPlugin::class.java)

      dependencies.attributesSchema.attribute(Usage.USAGE_ATTRIBUTE) {
        it.compatibilityRules.add(JavaIsValidForClass2MC::class.java)
        it.disambiguationRules.add(Class2MCPreferred::class.java)
      }

      sourceSetsOf(project).all { sourceSet ->
        addDeclarationConfigTo(sourceSet)
        createClasspathConfig(sourceSet)
        createOutgoingApiElementsConfig(sourceSet)
        connectDependencyConfigsOf(sourceSet)
      }

      makeMainModelsAvailableInTests()
    }
  }

  private fun sourceSetsOf(project: Project): SourceSetContainer =
    project.extensions.getByType(JavaPluginExtension::class.java).sourceSets

  /**
   * Declarable-only bucket: `class2mc(...)`.
   */
  private fun addDeclarationConfigTo(sourceSet: SourceSet): Configuration =
    project.configurations.maybeCreate(sourceSet.class2mcConfigName).apply {
      isCanBeConsumed = false
      isCanBeResolved = false
      isVisible = true
      description =
        "Declares Java dependencies usable from models in source set '${sourceSet.name}'."
    }

  /**
   * Resolves the actual jars for whoever needs to see these classes at generation time.
   */
  private fun createClasspathConfig(sourceSet: SourceSet): Configuration =
    project.configurations.maybeCreate(sourceSet.class2mcClasspathConfigName).apply {
      isCanBeConsumed = false
      isCanBeResolved = true
      isVisible = false
      description =
        "Resolves Java dependencies usable from models in source set '${sourceSet.name}'."
      attributes { attachClass2MCAttributes(it, project, LibraryElements.CLASSES) }
    }

  /**
   * Creates the dedicated outgoing Class2MC variant.
   *
   * It carries no artifact of its own and forwards dependencies declared via
   * `class2mc(...)` transitively to Class2MC consumers. The regular Java API
   * variant forwards them as well when the java-library plugin is applied,
   * because generated Java code may expose them in its public API.
   */
  private fun createOutgoingApiElementsConfig(sourceSet: SourceSet): Configuration =
    project.configurations.maybeCreate(sourceSet.class2mcApiElementsConfigName).apply {
      isCanBeConsumed = true
      isCanBeResolved = false
      isVisible = false
      description =
        "Forwards Java dependencies usable from models in source set '${sourceSet.name}' transitively."
      attributes { attachClass2MCAttributes(it, project, LibraryElements.JAR) }
    }

  private fun connectDependencyConfigsOf(sourceSet: SourceSet) = with(project) {
    val class2mc = configurations.named(sourceSet.class2mcConfigName)

    configurations.named(sourceSet.class2mcClasspathConfigName)
      .configure { it.extendsFrom(class2mc.get()) }
    configurations.named(sourceSet.class2mcApiElementsConfigName)
      .configure { it.extendsFrom(class2mc.get()) }
    configurations.named(sourceSet.implementationConfigurationName)
      .configure { it.extendsFrom(class2mc.get()) }

    pluginManager.withPlugin("java-library") {
      if (SourceSet.isMain(sourceSet)) {
        configurations.named(sourceSet.apiConfigurationName)
          .configure { it.extendsFrom(class2mc.get()) }
      }
    }
  }

  private fun makeMainModelsAvailableInTests() = with(project) {
    val sourceSets = sourceSetsOf(project)
    val main = sourceSets.named(SourceSet.MAIN_SOURCE_SET_NAME)
    val test = sourceSets.named(SourceSet.TEST_SOURCE_SET_NAME)
    configurations.named(test.get().class2mcConfigName).configure {
      it.extendsFrom(configurations.named(main.get().class2mcConfigName).get())
    }
  }
}
