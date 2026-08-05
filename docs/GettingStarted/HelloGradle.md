---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Hello, Gradle!

Most Java projects use [Gradle](https://gradle.org/). It makes working with larger projects and dependencies easier.
Likewise, MontiArc provides plugins for Gradle such that you can easily work with the tool and other libraries. 


## Creating a Project with Gradle
Navigate to a directory where you want to create your project. Create a new project by running: 
```bash
montiarc create MyMontiArcProject
```
This will create a new directory called `MyMontiArcProject` which contains a near-empty MontiArc Gradle project.

Or [download the template](https://github.com/MontiCore/montiarc/releases/download/snapshot/empty.zip) unzip and copy it into your folder.

Inside the project open `src/main/montiarc/pkg/HelloWorld.arc`:

```montiarc
package pkg;

import montiarc.lang.*;

component HelloWorld {
  automaton {
    initial state S;
    S -> S / {
      Console.printLn("Hello World!");
      Simulation.stop();
    }
  }
}
```

The code should be familiar to you. It is the ["Hello, World!"](./HelloWorld.md) model.

The Gradle plugin expects all MontiArc sources to be inside `src/main/montiarc`.

## Building and running with Gradle

To set up the correct Gradle wrapper version, you can run the following once:

=== "Windows"
    ```powershell
    .\init-gradlew.bat
    ```
=== "macOS/Linux"
    ```bash
    ./init-gradlew
    ```

To compile the model, simply execute the Gradle build task with the now added Gradle wrapper:

=== "Windows"
    ```powershell
    .\gradlew.bat build
    ```
=== "macOS/Linux"
    ```bash
    ./gradlew build
    ```

This command generates all Java files into the `build` folder of the project. You can now execute
the model, like you did before, by running:

=== "Windows"
    ```powershell
    .\gradlew.bat run -PmainClass="pkg.DeployHelloWorld"
    ```
=== "macOS/Linux"
    ```bash
    ./gradlew run -PmainClass="pkg.DeployHelloWorld"
    ```

The output should contain:
```output
> Task :run
Hello World!
```
