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

// Add the generated java code to your java source set
sourceSets {
  main {
    java {
      srcDir "where/montiarc/code/is/generated/to" // default value: $buildDir/montiarc
    }
  }
}

// Make your compile task (in this case it's compileJava) depend on the generation task
tasks.compileJava.configure { dependsOn(tasks.compileMontiarc) }
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

// Add the generated java code to your java source set
sourceSets["main"].java {
  srcDir("where/montiarc/code/is/generated/to")  // default value: $buildDir/montiarc
}

// Make your compile task (in this case it's compileJava) depend on the generation task
tasks.compileJava { dependsOn(tasks.compileMontiarc) }
```
---
### Configure it:
```groovy
// Groovy:
montiarc {
  modelPath.from("${projectDir}/main/resources")
  symbolImportDir.from("${projectDir}/main/resources")
  useClass2Mc.set(true)
  hwcPath.from("${projectDir}/main/java")
  outputDir.set("${buildDir}/montiarc")

  // You can use the following parameter to control the default values of modelPath, symbolImportDir, and hwcPath
  sourceSetName.set("maybeNotMain")
    
  // Only use the following option if you build the MontiArc project!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```
```kotlin
// Kotlin:
montiarc {
  modelPath.from("${projectDir}/main/resources")
  symbolImportDir.from("${projectDir}/main/resources")
  useClass2Mc.set(true)
  hwcPath.from("${projectDir}/main/java")
  outputDir.set("${buildDir}/generated-sources/montiarc")  // relative from the build directory

  // You can use the following parameter to control the default values of modelPath, symbolImportDir, and hwcPath
  sourceSetName.set("maybeNotMain")
  
  // Only use the following option if you build the MontiArc project!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```

### Configuration options in detail:

| Option                  | Default value                                                                                                                                                                                                                                                                                                        | Description                                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelPath               | `$projectDir/src/SOURCE_SET_NAME/montiarc`                                                                                                                                                                                                                                                                           | Where to find the MontiArc models for which Java code should be generated. You can specify multiple locations with multiple _from_ statements.                                                                                                                                                                                                                                         |
| hwcPath                 | If you do not apply the java plugin: `$projectDir/src/SOURCE_SET_NAME/java` <br/> If you apply the java plugin, then the hwcPath defaults to the java source in the SourceSet given by SOURCE_SET_NAME. An exception is the outputDir, which, even when in the SourceSet, will not be included when setting hwcPath. | Where to find the handwritten code extensions for the generated MontiArc code.                                                                                                                                                                                                                                                                                                         |
| symbolImportDir         | `$projectDir/src/SOURCE_SET_NAME/symbols`                                                                                                                                                                                                                                                                            | If you want to use `.sym` files, then you can use this configuration parameter to inform the generator where to find them. You can specify multiple locations with multiple _from_ statements.                                                                                                                                                                                         |
| useClass2Mc             | `false`                                                                                                                                                                                                                                                                                                              | If you want to use java types (or other JVM types) in your MontiArc models, then you set this configuration parameter to `true`. By this, all JVM types that are on the class path of the generator (which is the configuration `maGenerator`) will be accessible from MontiArc models.                                                                                                | <!-- TODO: Check if we need to put these types into the generateMA configuration -->
| outputDir               | `$buildDir/montiarc`                                                                                                                                                                                                                                                                                                 | Where the generated files should be placed.                                                                                                                                                                                                                                                                                                                                            |
| sourceSetName           | `main`                                                                                                                                                                                                                                                                                                               | Configures other default values: the _modelPath_, the _symbolImportDir_, and the _hwcPath_. It is used to refer to a specific source set in which the montiarc models and their handwritten code extensions lay.                                                                                                                                                                       |
| internalMontiArcTesting | `false`                                                                                                                                                                                                                                                                                                              | If you are not a MontiArc developer, just ignore it (and omit the option, as its default value is `false`). Otherwise: if you want to apply the plugin from within the MontiArc project, e.g., to test the generator in an integration test, then set this to `true`. By this, the freshly compiled (unpublished) generator will be used instead of the one from the maven repository. |

---
### Added tasks and configurations
* The Plugin adds the dependency configuration *maGenerator* on which it places the dependency on the generator that is
  used to generate .java code from the .arc files. If you use `class2mc`, then you can place the java classes that you
  want to use in your models on this configuration.
* The Plugin adds the task *compileMontiarc* which performs the generation step from .arc files to .java code.

---
### Fixing problems:
* The generator only generates classes with a *TOP* postfix\
    When you use the default value of the *hwcPath*, the *outputDir* may erroneously be in the hwcPath, because 
    filtering the outputDir went wrong. In this case the generator thinks that the previously generated files are
    handwritten extensions. To fix this, you should set the *hwcPath* manually. This will overwrite the default value,
    including the entry with the outputDir.

