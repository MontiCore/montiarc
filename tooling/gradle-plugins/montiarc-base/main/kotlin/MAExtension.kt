/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.montiarc

import org.gradle.api.provider.Property

/**
 * Makes configuration of the montiarc plugins easier (using montiarc { ... } in the build script)
 */
abstract class MAExtension {

  @Deprecated("do not use")
  abstract val internalMontiArcTesting: Property<Boolean>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}
