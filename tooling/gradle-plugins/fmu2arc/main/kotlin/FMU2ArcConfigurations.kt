/* (c) https://github.com/MontiCore/monticore */
package montiarc.gradle.fmu2arc

import org.gradle.api.tasks.SourceSet

/**
 * Usage value identifying variants that contain compiled FMU2Arc symbols.
 */
const val FMU2ARC_API_SYMBOL_USAGE = "fmu2arc-api"

/**
 * Usage value identifying variants that contain FMU files.
 */
const val FMU2ARC_API_FILE_USAGE = "fmu2arc-files"

/**
 * Base classifier for JARs containing compiled FMU2Arc symbols.
 */
const val FMU2ARC_SYMBOLS_BASE_CLASSIFIER = "fmu2arcSymbols"

/**
 * Base classifier for JARs containing FMU files.
 */
const val FMU2ARC_FILES_BASE_CLASSIFIER = "fmu2arcFiles"

/**
 * Name of the configuration used to declare dependencies on FMU2Arc model projects.
 *
 * For example, this is `fmu2arc` for `main` and `testFmu2arc` for `test`.
 */
val SourceSet.fmu2ArcConfigName: String
  get() = nameFMU2ArcConfig()

/**
 * Name of the resolvable configuration containing FMU files required on the model
 * path while compiling FMU2Arc models.
 *
 * For example, this is `fmu2arcModelpath` for `main` and
 * `testFmu2arcModelpath` for `test`.
 */
val SourceSet.fmu2ArcModelpathConfigName: String
  get() = nameFMU2ArcConfig("Modelpath")

/**
 * Name of the consumable configuration exposing compiled FMU2Arc symbols.
 *
 * For example, this is `fmu2arcApiElements` for `main` and
 * `testFmu2arcApiElements` for `test`.
 */
val SourceSet.fmu2ArcApiElementsConfigName: String
  get() = nameFMU2ArcConfig("ApiElements")

/**
 * Name of the consumable configuration exposing FMU files.
 *
 * For example, this is `fmu2arcFilesElements` for `main` and
 * `testFmu2arcFilesElements` for `test`.
 */
val SourceSet.fmu2ArcFilesElementsConfigName: String
  get() = nameFMU2ArcConfig("FilesElements")

/**
 * Name of the task that packages compiled FMU2Arc symbols into a JAR.
 */
val SourceSet.fmu2ArcSymbolsJarTaskName: String
  get() = getTaskName("fmu2Arc", "symbolsJar")

/**
 * Name of the task that packages FMU files into a JAR.
 */
val SourceSet.fmu2ArcFilesJarTaskName: String
  get() = getTaskName("fmu2Arc", "filesJar")

/**
 * Archive classifier for the JAR containing compiled FMU2Arc symbols.
 */
val SourceSet.fmu2ArcSymbolsJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
    FMU2ARC_SYMBOLS_BASE_CLASSIFIER
  } else {
    "$name-$FMU2ARC_SYMBOLS_BASE_CLASSIFIER"
  }

/**
 * Archive classifier for the JAR containing FMU files.
 */
val SourceSet.fmu2ArcFilesJarClassifierName: String
  get() = if (SourceSet.isMain(this)) {
    FMU2ARC_FILES_BASE_CLASSIFIER
  } else {
    "$name-$FMU2ARC_FILES_BASE_CLASSIFIER"
  }

private fun SourceSet.nameFMU2ArcConfig(suffix: String = ""): String =
  if (SourceSet.isMain(this)) "fmu2arc$suffix" else "${name}Fmu2arc$suffix"
