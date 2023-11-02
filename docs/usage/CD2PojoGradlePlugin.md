<!-- (c) https://github.com/MontiCore/monticore -->

This plugin generates Java code from cd2pojo models.
Moreover, it distributes cd2pojo models as jars with `.cdsym` files.

---
## Apply it:
Within gradle's settings script, you need to declare:

With Groovy:
```kotlin
// The plugin is in the Maven repo of the chair of Software Engineering at RWTH Aachen.
// Therefore we have to make it available to our build process.
pluginManagement {
  repositories {
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
  }
}
```

With Groovy:
```groovy
// The plugin is in the Maven repo of the chair of Software Engineering at RWTH Aachen.
// Therefore we have to make it available to our build process.
pluginManagement {
  repositories {
    maven {
      url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
    }
  }
}
```

Then, within the build script, you need to declare:

With Kotlin:
```kotlin
plugins {
  id("cd2pojo") version "VERSION_YOU_WANT_TO_USE"
}

// The generator is in the Maven repo of the chair of Software Engineering at RWTH Aachen
repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}
```

With Groovy:
```groovy
plugins {
  id "cd2pojo" version "VERSION_YOU_WANT_TO_USE"
}

// The generator is in the Maven repo of the chair of Software Engineering at RWTH Aachen
repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}
```

---
## Configure it:
The plugin adds a CD2PojoCompile task for every source set that exists.
The name of that task is `compileSRC_SET_NAMECd2pojo`, omitting the name of the source set for the *main* source set.

Note that Java source files that the cd2pojo generator produces are automatically added to the Java source of the same source set.
Therefor, the generated source code will automatically be compiled by `compileJava` (or `compileTestJava`, etc.).

With Kotlin:
```kotlin
// Kotlin:
sourceSets {
  main {
    cd2pojo {
      srcDir("where/your/cd2pojo/models/are") // default value: $projectDir/src/SOURCE_SET_NAME/cd2pojo
      destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/cd2pojo/SOURCE_SET_NAME
    }
  }
}

// Declare dependencies on published models
dependencies {
  cd2pojo("some.model.publisher:factory-types:1.2.0")
  testCd2pojo("some.testModel.publisher:factory-testers:1.2.0")
}

task.compileCd2pojo {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Cd2pjo"
  symbolImportDir.from("${projectDir}/src/SRC_SET_NAME/more_symbols")
  useClass2Mc.set(true)  // Default value is false
}

cd2pojo {
  // Only use the following option if you build the MontiArc project itself!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```

With Groovy:
```groovy
sourceSets {
  main {
    cd2pojo {
      srcDir "where/your/cd2pojo/models/are"  // default value: $projectDir/src/SOURCE_SET_NAME/cd2pojo
      destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/cd2pojo/SOURCE_SET_NAME
    }
  }
}

// Declare dependencies on published models
dependencies {
    cd2pojo "some.model.publisher:factory-types:1.2.0"
    testCd2pojo "some.testModel.publisher:factory-testers:1.2.0"
}

task.compileCd2pojo {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Cd2pojo"
  symbolImportDir.from("${projectDir}/src/SRC_SET_NAME/more_symbols")
  useClass2Mc.set(true)  // Default value is false
}

cd2pojo {
  // Only use the following option if you build the MontiArc project itself!
  // Else ignore it (You can just omit the option).
  internalMontiArcTesting.set(true)
}
```

Note that the generated java code will be generated to `$destinationDirectory/java` and created `.cdsym` files are
placed in `$destinationDirectory/symbols` (`destinationDirectory` being defined in the `cd2pojo` entry of the source
set).



## Configuration options in detail:
Each CD2PojoComile task has the following configuration options that can be set.
Some configuration options only have default values, if the task is created for a source set.

| Option          | Default value                                                | Description                                                                                                                                                                                                                                                                                                                                        |
|-----------------|--------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelPath       | `$projectDir/src/SOURCE_SET_NAME/cd2pojo`                    | Where to find the class diagram models for which Java code should be generated. You can specify multiple locations with multiple `modelPath.from(...)` statements.                                                                                                                                                                                 |
| hwcPath         | All java code from the same source set (main, or test, etc.) | Where to find the handwritten code extensions for the generated class diagram code.                                                                                                                                                                                                                                                                |
| symbolImportDir | The `cd2pojoSymbolDependencies` configuration                | If you want to use `.sym` files, then you can use this configuration parameter to inform the generator where to find them. You can specify multiple locations with multiple `symbolImportDir.from(...)` statements.                                                                                                                                |
| useClass2Mc     | `false`                                                      | If you want to use java types (or other JVM types) in your class diagram models, then you set this configuration parameter to `true`. By this, all JVM types that are on the class path of the generator (which is the configuration `cd2pojoGenerator`) will be accessible from class diagram models. *Note*: this will be changed in the future. |
| outputDir       | `$buildDir/cd2pojo/SOURCE_SET_NAME`                          | Where the generated files should be placed. Generated Java code ist placed in the `java` subfolder, exported symbol files are put in the `symbols` subfolder.                                                                                                                                                                                      |

Moreover, there are the following options configurable in the `cd2pojo` block:

| Option                  | Default value | Description                                                                                                                                                                                                                                                                                                                                                                            |
|-------------------------|---------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| internalMontiArcTesting | `false`       | If you are not a MontiArc developer, just ignore it (and omit the option, as its default value is `false`). Otherwise: if you want to apply the plugin from within the MontiArc project, e.g., to test the generator in an integration test, then set this to `true`. By this, the freshly compiled (unpublished) generator will be used instead of the one from the maven repository. |

---
## Added build elements
* The Plugin adds the dependency configuration *cd2pojoGenerator* on which it places the dependency on the generator 
  that is used to generate .java code from the .cd files. If you use `class2mc`, then you can place the java classes
  that you want to use in your models on this configuration.
* For every SourceSet, the plugin
  * Adds a CD2PojoCompile task that performs the generation step from .cd models
    to .java code. The task name is `compileCd2pojo` for main and `compileSourceSetNameCd2pojo` for others
  * Adds a jar task packaging the .cdsym models of the main source set, adding it to the default publication.
  * Adds three configurations:
    * `cd2pojo` for the main source set and `sourceSetNameCd2Pojo` for others:\
      Used to declare dependencies on other cd2pojo models. This configuration is not resolvable or consumable and only
      serves the purpose to declare dependencies. The models of the dependencies will automatically be added to the
      symbolImportDir of the `compileCd2pojo` task and their java implementations will automatically be added to the
      `implementation` configuration for compilation and runtime. To this end, `implementation` extends the `cd2pojo`
      configuration, as do:
    * `cd2pojoSymbolDependencies` for the main source set and `sourceSetNameCd2pojoSymbolDependencies` for others:\
      Only contains the cd models of the dependencies (represented by .cdsym files). Do not use
      this configuration to _declare_ the dependencies (use `cd2pojo` for this purpose instead), but use this
      configuration, when you want to access the cd2pojo models of the dependencies. (This configuration is derived
      from the `cd2pojo` configuration, only considering the cd2pojo models.)
    * `cd2pojoSymbolElements` for the main source set and `sourceSetNameCd2pojoSymbolElements` for others:\
      Contains the jar of the cd2pojo models (represented by .cdsym files) that is added to the default publication
      set. By default, this configuration is only added for the main source set.
  * Models declared in the main source set are also available in the test source sets.

---
## Fixing problems:
* The generator only generates classes with a *TOP* postfix\
  When you use the default value of the *hwcPath*, the *outputDir* may erroneously be in the hwcPath, because
  filtering the outputDir went wrong. In this case the generator thinks that the previously generated files are
  handwritten extensions. To fix this, you should set the *hwcPath* manually. This will overwrite the default value,
  including the entry with the outputDir.
* There is no _main_ or _test_ source set\
  These source sets only exist if the _java_ plugin is applied. If only the _java-base_ plugin is applied, then there
  are no _main_ or _tests_ source sets.
* If your dependencies use class2mc, then you have to activate it for your own project, too.
* If you activate class2mc for your main source set, you also have to activate it for your test source set.