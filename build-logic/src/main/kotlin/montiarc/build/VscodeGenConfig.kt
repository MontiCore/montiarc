/* (c) https://github.com/MontiCore/monticore */
package montiarc.build

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

/**
 * Extension for language-server projects for easier configuration of bundled
 * the vscode extension.
 */
abstract class VscodeGenConfig {
  /**
   * Where the extension project is generated to. Setting this property won't
   * set the generation location! But please set the location with this property
   * for further automation of the generation logic.
   */
  abstract val extensionProjectLocation: DirectoryProperty

  /** Source location of the icon used for MontiArc files in the VS code editor */
  abstract val icon : RegularFileProperty

  /** Source location of the Readme used for the extension */
  abstract val readme : RegularFileProperty

  /**
   * Location of vs code configurations for, e.g., MontiArc snippets,
   * code folding, bracket pairs, comments, etc.
   */
  abstract val languageConfig : ConfigurableFileCollection

  /**
   * Whether multiple languages are aggregated for this language server
   */
  abstract val multiproject : Property<Boolean>
}
