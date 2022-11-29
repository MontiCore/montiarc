/* (c) https://github.com/MontiCore/monticore */
package montiarc.tooling.plugin

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

/**
 * Makes configuration of the montiarc plugin easier (using montiarc { ... } in the build script)
 */
abstract class MAExtension {

  abstract val internalMontiArcTesting: Property<Boolean>

  @Deprecated("Use the modelPath of the MontiarcCompile task instead. Or better: put your models into " +
      "sourceSets { NAME { montiarc {...} } }.")
  abstract val modelPath : ConfigurableFileCollection

  @Deprecated("Use the symbolImportDir property of MontiarcCompile task instead.")
  abstract val symbolImportDir : ConfigurableFileCollection

  @Deprecated("Use the useClass2Mc property of the MontiarcCompile task instead.")
  abstract val useClass2Mc : Property<Boolean>

  @Deprecated("Use the hwcPath property of the MontiarcCompile task instead. Or put your hwc sources into " +
      "sourceSets { NAME { java {...} } }.")
  abstract val hwcPath : ConfigurableFileCollection

  @Deprecated("Use the outputDir of the MontiarcCompile task instead. Or define it in " +
      "sourceSets { NAME { montiarc { destinationDirectory.set(...) } } }")
  abstract val outputDir : Property<String>

  @Deprecated("You can now directly declare montiarc sources in sourceSets { NAME { montiarc {...} } } block")
  abstract val sourceSetName : Property<String>

  init {
    this.internalMontiArcTesting.convention(false)
  }
}