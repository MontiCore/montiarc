/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

abstract class MAExtension {

  abstract val internalMontiArcTesting: Property<Boolean>

  abstract val modelPath : ConfigurableFileCollection

  abstract val symbolImportDir : ConfigurableFileCollection

  abstract val useClass2Mc : Property<Boolean>

  abstract val hwcPath : ConfigurableFileCollection

  abstract val outputDir : Property<String>

  abstract val sourceSetName : Property<String>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}