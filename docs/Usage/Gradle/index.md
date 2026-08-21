---
icon: simple/gradle
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Using Gradle
Using [Gradle] to process MontiArc models brings some advantages, such as:

* Easier configuration of model processing
* Dependency management that allows you to publish models and to depend on other MontiArc libraries
* Automatic compilation of MontiArc's generated code (if the [_java_][Java Gradle Plugin] plugin is also applied)

## Perquisites
* Gradle 8.14.4 (or a [Gradle wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) with that version) is required to execute the MontiArc build process.
* Java 21 is the version of the code produced by the MontiArc generator.

## MontiArc Project Structure
When using Gradle to build MontiArc applications, the default project structure looks like the following:
```
project-root
├── src
│   ├── main
│   │   ├── java        // Present if you also apply the java plugin
│   │   ├── montiarc
│   │   │   └── com
│   │   │       └── example
│   │   │           ├── ComponentA.arc
│   │   │           └── ComponentB.arc
│   │   └── resources   // If you also apply the java plugin
│   │
│   └── test
│       ├── java        // If you also apply the java plugin
│       ├── montiarc
│       └── resources   // If you also apply the java plugin
│
├── build
│   ├── ...
│   └── montiarc
│       ├── main
│       │   ├── java     // Generated code remains here
│       │   └── symbols  // Here, .symarc files are placed.
│       │                // These are variants of your models that are meant for distribution
│       └── test
│           ├── java
│           └── symbols
│
├── build.gradle
└── settings.gradle
```
For every source set (e.g., `main` or `test` if you apply the Java plugin), your MontiArc models can be placed under the path `src/SOURCE_SET_NAME/montiarc`, e.g., `src/main/montiarc`.
Similar to Java, MontiArc models are organized in package structures that are also reflected by the directory structure in which they are saved.
E.g. if a model is in the package `com.example`, then its file should be under the path `montiarc/com/example`.
You can also [change the location](#further-references) in which models are found.

## Project setup
To enable the usage of the MontiArc plugin, we have to add the repository in which it lays to the settings file of Gradle:

[//]: # ( @formatter:off)

=== "Kotlin" 
    ```kotlin title="settings.gradle.kts"
    // The generator is in the Maven repo of the chair of Software Engineering at RWTH Aachen.
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
    ```groovy title="settings.gradle"
    // The generator is in the Maven repo of the chair of Software Engineering at RWTH Aachen.
    // Therefore we have to make it available to our build process.
    pluginManagement {
      repositories {
        maven {
          url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
        }
      }
    }
    ```

We can then apply the plugin by adding the following to Gradle's Build file:

=== "Kotlin"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("java")  // Optional, but recommended
      id("montiarc-jsim") version "VERSION_YOU_WANT_TO_USE"
    }

    // Required RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen.
    // Therefore we have to add this repo to our build.
    repositories {
      maven {
        url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
      }
    }
    ```
=== "Groovy"
    ```groovy title="build.gradle"
    plugins {
      id "java"  // Optional, but recommended
      id "montiarc-jsim" version "VERSION_YOU_WANT_TO_USE"
    }

    // Required RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen.
    // Therefore we have to add this repo to our build.
    repositories {
      maven {
        url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
      }
    }
    ```

[//]: # ( @formatter:on)

You can now place your models in the `src/main/montiarc` folder of your project.

## Executing the generation process
Gradle can be run as a standalone tool from the command line, or from IDEs by using plugins:

* IntelliJ IDEA comes with the [Gradle and Gradle Extension][IntelliJ Plugins] plugins shipped by default
* Eclipse gains Gradle support using the [Gradle Buildship Plugin][Eclipse Plugin]
* VS Code gains Gradle support using the [Gradle for Java Extension][VS Code Plugin]

`compileMontiarc` is the Gradle task that executes the MontiArc generation process.
Executing the `build` task will also trigger the generation.
It will additionally compile the generated Java code if the [_java_][Java Gradle Plugin] plugin is also applied.

Try creating a simple MontiArc model at the location of `src/main/montiarc/com/example/MyComp.arc`:
```montiarc
package com.example;

component MyComp { }
```

Now execute the `compileMontiarc` task with Gradle.
Check that, as a result, the Java class `MyComp` should be generated to `build/montiarc/main/java/com/example/MyComp.java`.

If you also want that the `build` task compiles the generated code, then also apply the [_java_][Java Gradle Plugin] plugin in your build script (if you have not done this yet):

[//]: # ( @formatter:off)

=== "Kotlin"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("java")
      id("montiarc-jsim") version "VERSION_YOU_WANT_TO_USE"
    }
    ```
=== "Groovy"
    ```groovy title="build.gradle"
    plugins {
      id "java"
      id "montiarc-jsim" version "VERSION_YOU_WANT_TO_USE"
    }
    ```

[//]: # ( @formatter:on)

## Adding class diagrams to your project
You can declare class diagram models to be used by MontiArc by applying the _cd2pojo_ plugin.
To do this, add the cd2pojo plugin to the `plugins` block within the build script:

[//]: # ( @formatter:off)

=== "Kotlin"
    ```kotlin title="build.gradle.kts"
    plugins {
      id("java")
      id("montiarc-jsim") version "VERSION_YOU_WANT_TO_USE"
      id("cd2pojo") version "VERSION_YOU_WANT_TO_USE"
    }
    ```
=== "Groovy"
    ```groovy title="build.gradle"
    plugins {
      id "java"
      id "montiarc-jsim" version "VERSION_YOU_WANT_TO_USE"
      id "cd2pojo" version "VERSION_YOU_WANT_TO_USE"
    }
    ```

[//]: # ( @formatter:on)

The project structure of projects with class diagrams is similar to [projects with MontiArc](#montiarc-project-structure) (but can also be [customized](#further-references)):
```bash
project-root
├── src
│   ├── main
│   │   ├── cd2pojo
│   │   │   └── ...     // Put your class diagram models here
│   │   ├── java        // Present if you also apply the java plugin
│   │   ├── montiarc    // If you also apply the montiarc plugin
│   │   └── resources   // If you also apply the java plugin
│   │
│   └── test
│       └── ...
├── build
│   ├── ...
│   └── cd2pojo
│       ├── main
│       │   ├── java
│       │   └── symbols
│       └── test
│           └── ...
│
├── build.gradle
└── settings.gradle
```

Try creating a simple class diagram model at the location of `src/main/cd2pojo/com/example/MyTypes.cd`:
```classdiagram
package com.example;

classdiagram MyTypes {
  public enum Status {
    ACTIVE,
    STAND_BY;
  }
}
```

Now use the Enum type in your MontiArc model [`MyComp`](#executing-the-generation-process):
```montiarc
package com.example;

import com.example.MyTypes.Status;

component MyComp(Status initialStatus) { }
```

Execute the `compileMontiarc` task with Gradle.
Check that no error occurs.
Moreover, verify that the Java class `Status` has been generated to `build/cd2pojo/main/java/com/example/MyTypes/Status.java`.
Note: If an error occurs, try running Gradle's `clean` task before. 

## Using java types
You can use JDK classes from MontiArc models by setting the `useClass2Mc` option of the generation task in Gradle's build script:
```kotlin title="build.gradle(.kts)"
// The syntax is the same for Kotlin and Groovy build scripts
tasks.compileMontiarc {
  useClass2Mc.set(true)
}
```

Now use `String` in your MontiArc model [`MyComp`](#executing-the-generation-process):
```montiarc
package com.example;

// Types from java.lang are automatically imported.
// For types from other packages, import them
// E.g.: 
// import com.example.MyJavaType;

component MyComp(String prefix) {}
```

Execute the `compileMontiarc` task with Gradle.
Check that no error occurs.

Java types can be used similarly from within class diagram models.
To this end, the `useClass2Mc` option has to be set for the `compileCd2pojo` task that under the hood processes the class diagram models:
```kotlin title="build.gradle(.kts)"
// Add the following to your build file:
tasks.compileCd2pojo {
  useClass2Mc.set(true)
}
```

!!! note 
    If you use Java types in class diagrams, then you also have to set the `useClass2Mc` option for your MontiArc models.
    Otherwise, you will encounter errors.

## Further references 
Applying the MontiArc Gradle plugin provides further benefits, like publishing your MontiArc models and depending on the models of other people.
It also enables fine configuration options.
You can find comprehensive information about the MontiArc plugin under [MontiArc Gradle Plugin](./MontiArc.md) and about the cd2pojo plugin under [CD2Pojo Gradle Plugin](./CD2Pojo.md).

[Gradle]: https://gradle.org/
[IntelliJ Plugins]: https://www.jetbrains.com/help/idea/getting-started-with-gradle.html
[Eclipse Plugin]: https://marketplace.eclipse.org/content/buildship-gradle-integration
[VS Code Plugin]: https://code.visualstudio.com/docs/java/java-build#_gradle
[Java Gradle Plugin]: https://docs.gradle.org/current/userguide/java_plugin.html
[Nexus ma2jsim Location]: https://nexus.se.rwth-aachen.de/#browse/search=group.raw%3Dmontiarc.generators%20AND%20name.raw%3Dma2jsim
[Nexus cd2pojo Location]: https://nexus.se.rwth-aachen.de/#browse/search=group.raw%3Dmontiarc.generators%20AND%20name.raw%3Dcd2pojo
