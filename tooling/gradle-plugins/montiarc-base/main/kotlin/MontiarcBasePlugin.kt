/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies several montiarc plugins:
 * * [MontiarcSourcePlugin]
 * * [MontiarcPublicationPlugin]
 * * [TransitiveCdPublicationPlugin]
 * * [TransitiveFMUPublicationPlugin]
 */
@Suppress("unused")
class MontiarcBasePlugin : Plugin<Project> {

  override fun apply(project: Project){
    with (project) {
      pluginManager.apply("java-base")
      pluginManager.apply(MontiarcSourcesPlugin::class.java)
      pluginManager.apply(MontiarcPublicationPlugin::class.java)
      pluginManager.apply(TransitiveCdPublicationPlugin::class.java)
      pluginManager.apply(montiarc.gradle.fmu2arc.TransitiveFMUPublicationPlugin::class.java)
    }
  }
}
