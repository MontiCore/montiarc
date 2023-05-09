/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.cd2pojo.plugin

import org.gradle.api.provider.Property

/**
 * Makes configuration of the cd2pojo plugin easier (using cd2pojo { ... } in the build script)
 */
abstract class Cd2PojoExtension {

  abstract val internalMontiArcTesting: Property<Boolean>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}