<!-- (c) https://github.com/MontiCore/monticore -->
## MontiArc Gradle Plugin
This plugin generates Java code from MontiArc models.

### Table of contents
1. [Apply it](#apply-it)
2. [Configure it](#configure-it)
3. [Configuration options in detail](#configuration-options-in-detail)
4. [Added tasks and configurations](#added-tasks-and-configurations)
5. [Fixing problems](#fixing-problems)


---
### Apply it:
```groovy
// Groovy:
buildscript {
  dependencies {
    classpath "montiarc.tooling:plugin:VERSION_YOU_WANT_TO_USE"
  }
}
plugins {
  id "montiarc"   
}
```

```kotlin
// Kotlin:
// Add the generator to your buildscript classpath
buildscript {
  dependencies {
    classpath("montiarc.tooling:plugin:VERSION_YOU_WANT_TO_USE")
  }
}

plugins {
  id("montiarc")
}
```
---
### Configure it:
The plugin adds a [MontiarcCompile](./main/kotlin/MontiarcPlugin.kt) task for every source set that exists.
The name of that task is `compileSRC_SET_NAMEMontiarc`, omitting the name of the source set for the *main* source set.

Note that Java source files that the Montiarc generator produces are automatically added to the Java source of the same
source set. Therefor, the generated source code will automatically be compiled by `compileJava` (or `compileTestJava`,
etc. )
```groovy
// Groovy:
sourceSets {
  main {
    montiarc {
      srcDir "where/your/montiarc/models/are"  // default value: $projectDir/SOURCE_SET_NAME/main/montiarc
      destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/montiarc/SOURCE_SET_NAME
    }
  }
}

task.compileMontiarc {  // compile task for other sourceSets: "compileSRC_SET_NAMEMontiarc"
  symbolImportDir.from("${projectDir}/src/SRC_SET_NAME/symbols")  // ← Default value
  useClass2Mc.set(true)  // Default value is false
}

montiarc {
  // Only use the following option if you build the MontiArc project!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```
```kotlin
// Kotlin:
sourceSets {
  main {
    montiarc {
      srcDir("where/your/montiarc/models/are")  // default value: $projectDir/SOURCE_SET_NAME/main/montiarc
      destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/montiarc/SOURCE_SET_NAME
    }
  }
}

task.compileMontiarc {  // compile task for other sourceSets: "compileSRC_SET_NAMEMontiarc"
  symbolImportDir.from("${projectDir}/src/SRC_SET_NAME/symbols")  // ← Default value
  useClass2Mc.set(true)  // Default value is false
}

montiarc {
  // Only use the following option if you build the MontiArc project!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```

### Configuration options in detail:
Each [MontiarcCompile](./main/kotlin/MontiarcCompile.kt) task has the following configuration options that can be set.
Some configuration options only have default values, if they were created for a source set. 

| Option          | Default value                                                | Description                                                                                                                                                                                                                                                                                                                         |
|-----------------|--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelPath       | `$projectDir/src/SOURCE_SET_NAME/montiarc`                   | Where to find the MontiArc models for which Java code should be generated. You can specify multiple locations with multiple _from_ statements.                                                                                                                                                                                      |
| hwcPath         | All java code from the same source set (main, or test, etc.) | Where to find the handwritten code extensions for the generated MontiArc code.                                                                                                                                                                                                                                                      |
| symbolImportDir | `$projectDir/src/SOURCE_SET_NAME/symbols`                    | If you want to use `.sym` files, then you can use this configuration parameter to inform the generator where to find them. You can specify multiple locations with multiple _from_ statements.                                                                                                                                      |
| useClass2Mc     | `false`                                                      | If you want to use java types (or other JVM types) in your MontiArc models, then you set this configuration parameter to `true`. By this, all JVM types that are on the class path of the generator (which is the configuration `maGenerator`) will be accessible from MontiArc models. *Note*: this will be changed in the future. | <!-- TODO: Check if we need to put these types into the generateMA configuration -->
| outputDir       | `$buildDir/montiarc/SOURCE_SET_NAME`                         | Where the generated files should be placed.                                                                                                                                                                                                                                                                                         |
| sourceSetName   | _deprecated_ (None)                                          |                                                                                                                                                                                                                                                                                                                                     |

Moreover, there are the following options configurable in the `montiarc` block:

| Option                  | Default value | Description                                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| internalMontiArcTesting | `false`       | If you are not a MontiArc developer, just ignore it (and omit the option, as its default value is `false`). Otherwise: if you want to apply the plugin from within the MontiArc project, e.g., to test the generator in an integration test, then set this to `true`. By this, the freshly compiled (unpublished) generator will be used instead of the one from the maven repository. |

---
### Added tasks and configurations
* The Plugin adds the dependency configuration *maGenerator* on which it places the dependency on the generator that is
  used to generate .java code from the .arc files. If you use `class2mc`, then you can place the java classes that you
  want to use in your models on this configuration.
* The Plugin adds a [MontiarcCompile](./main/kotlin/MontiarcCompile.kt) task for every source set of the project
  (by default: _main_ and _test_). The task performs the generation step from .arc files to .java code.

---
### Fixing problems:
* The generator only generates classes with a *TOP* postfix\
    When you use the default value of the *hwcPath*, the *outputDir* may erroneously be in the hwcPath, because 
    filtering the outputDir went wrong. In this case the generator thinks that the previously generated files are
    handwritten extensions. To fix this, you should set the *hwcPath* manually. This will overwrite the default value,
    including the entry with the outputDir.

