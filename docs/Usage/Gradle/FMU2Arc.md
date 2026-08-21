<!-- (c) https://github.com/MontiCore/monticore -->

This plugin generates Java code from Functional Mockup Units.
Moreover, it distributes its models as jars together with `.arcsym` files.

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
        id("fmu2arc") version "VERSION_YOU_WANT_TO_USE"
      }

      // The generator is in the Maven repo of the chair of Software Engineering at RWTH Aachen
      repositories {
        maven {
          url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
        }
      }
    ```

=== "Groovy"
    ```groovy
      plugins {
        id "fmu2arc" version "VERSION_YOU_WANT_TO_USE"
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
The plugin adds a FMU2ArcCompile task for every source set that exists.
The name of that task is `compileSRC_SET_NAMEFmu2arc`, omitting the name of the source set for the *main* source set.

Note that Java source files that the fmu2arc generator produces are automatically added to the Java source of the same source set.
Therefore, the generated source code will automatically be compiled by `compileJava` (or `compileTestJava`, etc.).

=== "Kotlin"
    ```kotlin
    sourceSets {
      main {
        fmu2arc {
          srcDir("where/your/fmu2arc/models/are") // default value: $projectDir/src/SOURCE_SET_NAME/fmu2arc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/fmu2arc/SOURCE_SET_NAME
        }
      }
    }

    // Declare dependencies on published models
    dependencies {
      fmu2arc("some.model.publisher:factory-types:1.2.0")
      testFmu2arc("some.testModel.publisher:factory-testers:1.2.0")
    }

    tasks.compileFmu2arc {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Fmu2arc"
      modelpath.from("${projectDir}/src/SRC_SET_NAME/more_fmus")
    }
    ```

=== "Groovy"
    ```groovy
    sourceSets {
      main {
        fmu2arc {
          srcDir "where/your/fmu2arc/models/are"  // default value: $projectDir/src/SOURCE_SET_NAME/fmu2arc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/fmu2arc/SOURCE_SET_NAME
        }
      }
    }

    // Declare dependencies on published models
    dependencies {
      fmu2arc "some.model.publisher:factory-types:1.2.0"
      testFmu2arc "some.testModel.publisher:factory-testers:1.2.0"
    }

    tasks.compileFmu2arc {  // compile task for other sourceSets: "compile{SRC_SET_NAME}Fmu2arc"
      modelpath.from("${projectDir}/src/SRC_SET_NAME/more_fmus")
    }
    ```

Note that the generated Java code will be generated to `$destinationDirectory/java` and created `.arcsym` files are
placed in `$destinationDirectory/symbols` (`destinationDirectory` being defined in the `fmu2arc` entry of the source
set).



## Configuration options in detail:
Each FMU2ArcCompile task has the following configuration options that can be set.
Some configuration options only have default values if the task is created for a source set.

| Option          | Default value                                          | Description                                                                                                                                                   |
|-----------------|--------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelpath       | `$projectDir/src/SOURCE_SET_NAME/fmu2arc`              | Where to find the FMUs for which Java code should be generated. You can specify multiple locations with multiple `modelpath.from(...)` statements.            |
| outputDir       | `$buildDir/fmu2arc/SOURCE_SET_NAME`                    | Where the generated files should be placed. Generated Java code ist placed in the `java` subfolder, exported symbol files are put in the `symbols` subfolder. |
| debugTask       | `false`                                                | If set to true, a debugger can be attached to the generator process for debugging purposes.                                                                   |


## Added build elements
* The Plugin adds the dependency configuration *fmu2arcToolClasspath* on which it places the dependency on the generator
  that is used to generate .java code from the .fmu files. 
* For every SourceSet, the plugin
  * Adds a FMU2ArcCompile task that performs the generation step from .fmu files
    to .java code. The task name is `compileFmu2arc` for main and `compileSourceSetNameFmu2arc` for others
  * Adds a jar task packaging the .arcsym models of the main source set, adding it to the default publication.
  * Adds three configurations:
    * `fmu2arc` for the main source set and `sourceSetNameFMU2Arc` for others:
      Used to declare dependencies on other fmu2arc models. This configuration is not resolvable or consumable and only
      serves the purpose to declare dependencies. Their Java implementations will automatically be added to the
      `implementation` configuration for compilation and runtime. To this end, `implementation` extends the `fmu2arc`
      configuration, as do:
     * `fmu2arcApiElements` for the main source set and `sourceSetNameFMU2ArcApiElements` for others:\
      Contains the jar of the fmu2arc models (represented by .arcsym files) that is added to the default publication
      set. By default, this configuration is only added for the main source set.
     * `fmu2arcFilesElements` for the main source set and `sourceSetNameFMU2ArcFilesElements` for others:\
      Contains the jar of the fmu files needed to run the java code that is added to the default publication
  * Models declared in the main source set are also available in the test source sets.
