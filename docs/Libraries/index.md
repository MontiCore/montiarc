<!-- (c) https://github.com/MontiCore/monticore -->
# Libraries

Every library consists of component models and class definitions. 
Libraries are distributed as JAR files, and MontiArc provides means 
to automatically resolve and include different library dependencies by using [Gradle](../Usage/Gradle/index.md).

For a list of all publicly available libraries, go to [Public Libraries](./PublicLibraries/index.md).

The documentation for the standard library, which is included in every MontiArc project, can be seen [here](./StandardLibrary/index.md).

## Using and Publishing Libraries

This section provides details on how to include libraries in your project and how to publish your own library such that it can be consumed by others.
This guide assumes that you're using [Gradle](../Usage/Gradle/index.md) to manage your project.

### Using

MontiArc dependencies are added like any other [Gradle dependency](https://docs.gradle.org/current/userguide/declaring_dependencies.html).

Inside the dependencies block of your Gradle build file, you can use the `montiarc` configuration.

=== "build.gradle.kts"
    ```kotlin
    dependencies {
        // montiarc + Dependency Notation - GroupID : ArtifactID (Name) : Version
        montiarc('<group>:<name>:<version>')
    }
    ```

### Publishing

The easiest way to publish your own components is by using maven repositories. 
For that you need to enable the [`maven-publish`](https://docs.gradle.org/current/userguide/publishing_maven.html) plugin in your project
and create a publishing config.


=== "build.gradle.kts"
    ```kotlin
    plugins {
      `java-library`
      `maven-publish`
      id("montiarc-jsim") version "7.10.0-SNAPSHOT"
      id("cd2pojo") version "7.10.0-SNAPSHOT"
    }

    publishing {
      publications {
        create<MavenPublication>("maven") {
          groupId = "de.montiarc.sample"
          artifactId = "library"
          version = "1.0"

          from(components["java"])
        }
      }
    }
    ```

Running `gradle publish` will publish your library.

For more information about configuration and specifying the target repository, where the library is published to, see the official [Gradle documentation](https://docs.gradle.org/current/userguide/publishing_maven.html).
