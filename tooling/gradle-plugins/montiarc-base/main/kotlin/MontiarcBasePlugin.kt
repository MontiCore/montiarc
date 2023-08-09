/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project

const val DSL_EXTENSION_NAME = "montiarc"

/**
 * Applies several montiarc plugins:
 * * [MontiarcSourcePlugin]
 * * [MontiarcPublicationPlugin]
 * * [TransitiveCdPublicationPlugin]
 *
 * and adds the [MAExtension] to the project, facilitating montiarc configuration.
 */
@Suppress("unused")
class MontiarcBasePlugin : Plugin<Project> {

  override fun apply(project: Project){
    with (project) {
      pluginManager.apply("java-base")
      pluginManager.apply(MontiarcSourcesPlugin::class.java)
      pluginManager.apply(MontiarcPublicationPlugin::class.java)
      pluginManager.apply(TransitiveCdPublicationPlugin::class.java)

      extensions.create(DSL_EXTENSION_NAME, MAExtension::class.java)
    }
  }
}
