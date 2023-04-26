/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.cd2pojo.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

/**
 * Makes configuration of the montiarc plugin easier (using montiarc { ... } in the build script)
 */
abstract class Cd2PojoExtension {

  abstract val internalMontiArcTesting: Property<Boolean>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}