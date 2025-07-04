/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.sd2arc

import org.gradle.api.provider.Property

/**
 * Makes configuration of the sd2arc plugin easier (using sd2arc { ... } in the build script)
 */
abstract class Sd2ArcExtension {

  abstract val internalMontiArcTesting: Property<Boolean>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}