<!-- (c) https://github.com/MontiCore/monticore -->

This plugin generates Java code from MontiArc models.
Moreover, it distributes MontiArc models as jars with `.arcsym` files.

---
## Apply it:
Within Gradle's settings script, you need to declare:

=== "Kotlin"
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
=== "Groovy"
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

=== "Kotlin"
    ```kotlin
    plugins {
      id("montiarc-jsim") version "VERSION_YOU_WANT_TO_USE"
    }

    // The generator and RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen
    repositories {
      maven {
        url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
      }
    }
    ```

=== "Groovy"
    ```groovy
    plugins {
      id "montiarc-jsim" version "VERSION_YOU_WANT_TO_USE"
    }

    // The generator and RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen
    repositories {
      maven {
        url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
      }
    }
    ```

---
## Configure it:
The plugin adds a MontiArcCompile task for every source set that exists.
The name of that task is `compileSRC_SET_NAMEMontiarc`, omitting the name of the source set for the *main* source set.

Note that Java source files that the MontiArc generator produces are automatically added to the Java source of the same
source set. Therefore, the generated source code will automatically be compiled by `compileJava` (or `compileTestJava`,
etc. )

=== "Kotlin"
    ```kotlin
    // Kotlin:
    sourceSets {
      main {
        montiarc {
          srcDir("where/your/montiarc/models/are")  // default value: $projectDir/src/SOURCE_SET_NAME/montiarc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/montiarc/SOURCE_SET_NAME
        }
      }
    }

    // Declare dependencies on published models
    dependencies {
      montiarc("some.model.publisher:logic-gates:1.2.0")
      cd2pojo4montiarc("some.class:diagram.types:1.1.0")
      testMontiarc("some.testModel.publisher:logic-gates:1.2.0")
    }

    tasks.compileMontiarc {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Montiarc"
      symbolpath.from("${projectDir}/src/SRC_SET_NAME/more_symbols")
      useClass2Mc.set(true)  // Default value is false
    }
    ```

=== "Groovy"
    ```groovy
    sourceSets {
      main {
        montiarc {
          srcDir "where/your/montiarc/models/are"  // default value: $projectDir/src/SOURCE_SET_NAME/montiarc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/montiarc/SOURCE_SET_NAME
        }
      }
    }

    // Declare dependencies on published models
    dependencies {
        montiarc "some.model.publisher:logic-gates:1.2.0"
        cd2pojo4montiarc "some.class:diagram.types:1.1.0"
        testMontiarc "some.testModel.publisher:logic-gates:1.2.0"
    }

    tasks.compileMontiarc {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Montiarc"
      symbolpath.from("${projectDir}/src/SRC_SET_NAME/more_symbols")
      useClass2Mc.set(true)  // Default value is false
    }
    ```

Note that the generated java code will be generated to `$destinationDirectory/java` and created `.arcsym` files are
placed in `$destinationDirectory/symbols` (`destinationDirectory` being defined in the `montiarc` entry of the source
set).

## Configuration options in detail:
Each MontiArcCompile task has the following configuration options that can be set.
Some configuration options only have default values, if the task is created for a source set. 

| Option          | Default value                                                | Description                                                                                                                                                                                                                                                                                                                         |
|-----------------|--------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelpath       | `$projectDir/src/SOURCE_SET_NAME/montiarc`                   | Where to find the MontiArc models for which Java code should be generated. You can specify multiple locations with multiple `modelpath.from(...)` statements.                                                                                                                                                                       |
| hwcPath         | All java code from the same source set (main, or test, etc.) | Where to find the handwritten code extensions for the generated MontiArc code.                                                                                                                                                                                                                                                      |
| symbolpath      | The `montiarcSymbolpath` configuration                       | If you want to use `.sym` files, then you can use this configuration parameter to inform the generator where to find them. You can specify multiple locations with multiple `symbolpath.from(...)` statements.                                                                                                                 |
| useClass2Mc     | `false`                                                      | If you want to use java types (or other JVM types) in your MontiArc models, then you set this configuration parameter to `true`. By this, all JVM types that are on the class path of the generator (which is the configuration `ma2jsimToolClasspath`) will be accessible from MontiArc models. *Note*: this will be changed in the future. | <!-- TODO: Check if we need to put these types into the generateMA configuration --> |
| outputDir       | `$buildDir/montiarc/SOURCE_SET_NAME`                         | Where the generated files should be placed. Generated Java code ist placed in the `java` subfolder, exported symbol files are put in the `symbols` subfolder.                                                                                                                                                                       |
| debugTask       | `false`                                                      | If set to true, a debugger can be attached to the generator process for debugging purposes.                                                                                                                                                                                                                                         |
---
## Interplay with cd2pojo
If the [`cd2pojo` plugin](./CD2Pojo.md) Plugin is also applied, then the class diagram models defined therein
are also available in the MontiArc models of the same source set.

---
## Added build elements
* The Plugin adds the dependency configuration *ma2jsimToolClasspath* on which it places the dependency on the generator that is
  used to generate .java code from the .arc files. If you use `class2mc`, then you can place the java classes that you
  want to use in your models on this configuration.
* For every SourceSet, the plugin
  * Adds a `MontiArcCompile` task that performs the generation step from .arc models
    to .java code. The task name is `compileMontiarc` for main and `compileSourceSetNameMontiarc` for others
  * Adds a jar task packaging the .arcsym models of the main source set, adding it to the default publication.
  * Adds three configurations:
    * `montiarc` for the main source set and `sourceSetNameMontiarc` for others:\
      Used to declare dependencies on other MontiArc models. This configuration is not resolvable or consumable and only
      serves the purpose to declare dependencies. The models of the dependencies will automatically be added to
      the `compileMontiarc` task and their java implementations will automatically be added to the
      `implementation` configuration for compilation and runtime.
    * `cd2pojo4montiarc` for the main source set and `sourceSetNameCd2pojo4montiarc` for others:\
      Used to declare dependencies of MontiArc models on other class diagram projects. This configuration is not
      resolvable or consumable and only serves the purpose to declare dependencies. The models of the dependencies will
      automatically be added to the `compileMontiarc` task and their java implementations will automatically be added to
      the `implementation` configuration for compilation and runtime.
    * `montiarcSymbolpath` for the main source set and `sourceSetNameMontiarcSymbolpath` for others:\
      Loads the MontiArc dependencies (represented by .arcsym files). Do not use 
      this configuration to _declare_ the dependencies (use `montiarc` for this purpose instead), but use this
      configuration, when you want to access the MontiArc models of the dependencies. (This configuration is
      automatically derived from the `montiarc` configuration, only considering the montiarc models.)
    * `cd2pojo4montiarcSymbolpath` for the main source set and `sourceSetNameCd2pojo4montiarcSymbolpath`
      for others:\
      Loads the class diagram dependencies of our MontiArc models (represented by .cdsym files). Do not use
      this configuration to _declare_ the dependencies (use `cd2pojo4montiarc` for this purpose instead), but use this
      configuration, when you want to access the class diagram dependencies. (This configuration is
      automatically derived from the `cd2pojo4montiarc` configuration, only considering the class diagram models.)
    * `montiarcApiElements` for the main source set and `sourceSetNameMontiarcApiElements` for others:\
      Contains the jar of the MontiArc models (represented by .arcsym files) that is added to the default publication
      set. Is also used to publish the list of MontiArc dependencies of this project. To this end, it extends the
      `montiarc` configuration. By default, this configuration is only added for the main source set.
    * `cd2pojo4montiarcApiElements` for the main source set and
      `sourceSetNameCd2pojo4montiarcApiElements` for others:\
      Used to publish the list of class diagram dependencies of our MontiArc models. To this end, it extends the
      `cd2pojo4montiarc` configuration. By default, this configuration is only added for the main source set.
      If the project also applies the `cd2pojo` plugin, then the cd-symbols jar is also published with this
      configuration. This is due to technical reasons.
  * Models declared in the main source set are also available in the test source sets.

---
## Fixing problems:
* The generator only generates classes with a *TOP* postfix\
    When you use the default value of the *hwcPath*, the *outputDir* may erroneously be in the hwcPath because 
    filtering the outputDir went wrong. In this case, the generator thinks that the previously generated files are
    handwritten extensions. To fix this, you should set the *hwcPath* manually. This will overwrite the default value,
    including the entry with the outputDir.
* There is no _main_ or _test_ source set\
    These source sets only exist if the _java_ plugin is applied. If only the _java-base_ plugin is applied, then there
  are no _main_ or _tests_ source sets.
* If your dependencies use class2mc, then you have to activate it for your own project, too.
* If you activate class2mc for your main source set, you also have to activate it for your test source set.
