<!-- (c) https://github.com/MontiCore/monticore -->

This plugin generates MontiArc test models from sequence diagrams.
See the [testing](../../Reference/Testing/SequenceDiagrams.md) with sequence diagrams section for more information. 

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
      id("sd2arc") version "VERSION_YOU_WANT_TO_USE"
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
      id "sd2arc" version "VERSION_YOU_WANT_TO_USE"
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
The plugin adds a SD2ArcCompile task for every source set that exists.
The name of that task is `compileSRC_SET_NAMEsd2arc`, omitting the name of the source set for the *main* source set.

Note that MontiArc source files that the sd2arc generator produces are automatically added to the MontiArc source of the same source set.
Therefore, the generated models are processed together with user created models in the source set.
Hence, the sequence diagram can only reference MontiArc models not part of the same source set as they haven't been processed yet.

=== "Kotlin"
    ```kotlin
    sourceSets {
      main {
        sd2arc {
          srcDir("where/your/sd2arc/models/are") // default value: $projectDir/src/SOURCE_SET_NAME/sd2arc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/sd2arc/SOURCE_SET_NAME
        }
      }
    }
    ```

=== "Groovy"
    ```groovy
    sourceSets {
      main {
        sd2arc {
          srcDir "where/your/sd2arc/models/are"  // default value: $projectDir/src/SOURCE_SET_NAME/sd2arc
          destinationDirectory.fileValue(file("where/to/generate/the/code/to"))  // default value: $buildDir/sd2arc/SOURCE_SET_NAME
        }
      }
    }
    ```

Note that the generated Java code will be generated to `$destinationDirectory/montiarc` 
(`destinationDirectory` being defined in the `sd2arc` entry of the source set).



## Configuration options in detail:
Each SD2ArcCompile task has the following configuration options that can be set.
Some configuration options only have default values if the task is created for a source set.

| Option       | Default value                                | Description                                                                                                                                                                                                                                                                                                                                               |
|--------------|----------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| modelpath    | `$projectDir/src/SOURCE_SET_NAME/sd2arc`     | Where to find the sequence diagram models for which tests should be generated. You can specify multiple locations with multiple `modelpath.from(...)` statements.                                                                                                                                                                                         |
| symbolpath   | *(none)*                                     | Unlike cd2pojo/fmu2arc/MontiArc, sd2arc does not have its own dependency-declaration configuration for this. Instead, it is populated automatically: if the `cd2pojo` plugin is applied, `main`'s compiled class diagram symbols are added here; if the `montiarc-jsim` plugin is applied, `main`'s compiled MontiArc symbols are added here. You can specify additional locations yourself with `symbolpath.from(...)` statements.                                                                                                                                       |
| useClass2Mc  | `false`                                      | If you intend to use Java types (or other JVM types) in your sequence diagram models, then you set this configuration parameter to `true`. By this, all JVM types that are on the class path of the generator (which is the configuration `sd2arcToolClasspath`) will be accessible from sequence diagram models. *Note*: This will be changed in the future. |
| outputDir    | `$buildDir/sd2arc/SOURCE_SET_NAME`           | Where the generated files should be placed. Generated MontiArc files are placed in the `montiarc` subfolder, exported symbol files are put in the `symbols` subfolder.                                                                                                                                                                                    |
| debugTask    | `false`                                      | If set to true, a debugger can be attached to the generator process for debugging purposes.                                                                                                                                                                                                                                                               |
