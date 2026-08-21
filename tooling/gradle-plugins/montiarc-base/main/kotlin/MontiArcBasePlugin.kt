/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies several montiarc plugins:
 * * [MontiArcSourcesPlugin]
 * * [MontiArcPublicationPlugin]
 * * [TransitiveCDPublicationPlugin]
 * * [TransitiveFMUPublicationPlugin]
 */
@Suppress("unused")
class MontiArcBasePlugin : Plugin<Project> {

  override fun apply(project: Project){
    with (project) {
      pluginManager.apply("java-base")
      pluginManager.apply(MontiArcSourcesPlugin::class.java)
      pluginManager.apply(MontiArcPublicationPlugin::class.java)
      pluginManager.apply(TransitiveCDPublicationPlugin::class.java)
      pluginManager.apply(TransitiveFMUPublicationPlugin::class.java)
    }
  }
}
