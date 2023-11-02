<!-- (c) https://github.com/MontiCore/monticore -->

This page aims to help you to compile your first MontiArc models.
MontiArc projects can be developed with the [Gradle build tool](#using-gradle).
On the other hand, there is also a [CLI tool](#using-the-command-line) that you can use to process MontiArc models.

## Using Gradle
Using [Gradle] to process MontiArc models brings some advantages, such as:
* Easier configuration of model processing
* Dependency management that allows you to publish models and to depend on other MontiArc libraries
* Automatic compilation of MontiArc's generated code (if the [_java_][Java Gradle Plugin] plugin is also applied)

### Prequisites
* Gradle 7.5 is required to execute the MontiArc build process.
* Java 11 is the version of the code produced by the MontiArc generator.
* (MontiArc 7.5.0 mistakenly requires Java 17 when using the Gradle plugin.
  Use a higher MontiArc version if you want to use Java 11.)

### MontiArc project structure
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
For every source set (e.g. `main` or `test` if you apply the java plugin) your MontiArc models can be placed under the path `src/SOURCE_SET_NAME/montiarc`, e.g. `src/main/montiarc`.
Similar to Java, MontiArc models are organized in package structures that are also reflected by the directory structure in which they are saved.
E.g. if a model is in the package `com.example`, then its file should be under the path `montiarc/com/example`.
You can also [change the location](#further-references) in which models are found.

### Project setup
To enable the usage of the MontiArc plugin, we have to add the repository in which it lays to the settings file of gradle:

{{< tabs "gradle-settings-file" >}}
{{< tab "Kotlin" >}} 
```kotlin
// If your settings file is settings.gradle.kts (namely, it uses kotlin syntax)

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
{{< /tab >}}
{{< tab "Groovy" >}} 
```groovy
// If your settings file is settings.gradle (namely, it uses groovy syntax)

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
{{< /tab >}}
{{< /tabs >}}

We can then apply the plugin by adding the following to Gradle's Build file:
{{< tabs "gradle-build-file" >}}
{{< tab "Kotlin" >}} 
```kotlin
// Use this code if your build file is build.gradle.kts (namely, it uses kotlin syntax)
plugins {
  id("java")  // Optional, but recommended for this tutorial
  id("montiarc") version "VERSION_YOU_WANT_TO_USE"
}

// Required RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen.
// Therefore we have to add this repo to our build.
repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}
```
{{< /tab >}}
{{< tab "Groovy" >}} 
```groovy
// Use this code if your build file is build.gradle (namely, it uses groovy syntax)
plugins {
  id "java"  // Optional, but recommended for this tutorial
  id "montiarc" version "VERSION_YOU_WANT_TO_USE"
}

// Required RTE classes are in the Maven repo of the chair of Software Engineering at RWTH Aachen.
// Therefore we have to add this repo to our build.
repositories {
  maven {
    url = uri("https://nexus.se.rwth-aachen.de/content/groups/public/")
  }
}
```
{{< /tab >}}
{{< /tabs >}}

You can now place your models in the `src/main/montiarc` folder of your project.

### Executing the generation process
Gradle can be run as a standalone tool from the command line, or from IDEs by using plugins:
* IntelliJ IDEA comes with the [Gradle and Gradle Extension][IntelliJ Plugins] plugins shipped by default
* Eclipse gains Gradle support using the [Gradle Buildship Plugin][Eclipse Plugin]
* VS Code gains Gradle support using the [Gradle for Java Extension][VS Code Plugin]

`compileMontiarc` is the Gradle task that executes the MontiArc generation process.
Executing the `build` task will also trigger the generation.
It will additionally compile the generated Java code if the [_java_][Java Gradle Plugin] plugin is also applied.

Try creating a simple MontiArc model at the location of `src/main/montiarc/com/example/MyComp.arc`:
```
package com.example;

component MyComp { }
```

Now execute the `compileMontiarc` task with Gradle.
Check that, as a result, the java class `MyComp` should be generated to `build/montiarc/main/java/com/example/MyComp.java`.

If you also want that the `build` task compiles the generated code, then also apply the [_java_][Java Gradle Plugin] plugin in your build script (if you have not done this yet):
{{< tabs "gradle-apply-java" >}}
{{< tab "Kotlin" >}} 
```kotlin
// If your build file is build.gradle.kts (namely, it uses kotlin syntax)
plugins {
  id("java")
  id("montiarc") version "VERSION_YOU_WANT_TO_USE"
}
```
{{< /tab >}}
{{< tab "Groovy" >}} 
```groovy
// If your build file is build.gradle (namely, it uses groovy syntax)
plugins {
  id "java"
  id "montiarc" version "VERSION_YOU_WANT_TO_USE"
}
```
{{< /tab >}}
{{< /tabs >}}

### Adding class diagrams to your project
You can declare class diagram models to be used by MontiArc by applying the _cd2pojo_ plugin.
In order to do this, add the cd2pojo plugin to the `plugins` block within the build script:

{{< tabs "gradle-apply-cd2pojo" >}}
{{< tab "Kotlin" >}} 
```kotlin
// If your build file is build.gradle.kts (namely, it uses kotlin syntax)
plugins {
  id("java")
  id("montiarc") version "VERSION_YOU_WANT_TO_USE"
  id("cd2pojo") version "VERSION_YOU_WANT_TO_USE"
}
```
{{< /tab >}}
{{< tab "Groovy" >}} 
```groovy
// If your build file is build.gradle (namely, it uses groovy syntax)
plugins {
  id "java"
  id "montiarc" version "VERSION_YOU_WANT_TO_USE"
  id "cd2pojo" version "VERSION_YOU_WANT_TO_USE"
}
```
{{< /tab >}}
{{< /tabs >}}

The project structure of projects with class diagrams is similar to [projects with MontiArc](#montiarc-project-structure) (but can also be [customized](#further-references)):
```
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
```
package com.example;

classdiagram MyTypes {
  public enum Status {
    ACTIVE,
    STAND_BY;
  }
}
```

Now use the enum type in your MontiArc model [`MyComp`](#executing-the-generation-process):
```
package com.example;

import com.example.MyTypes.Status;

component MyComp(Status initialStatus) { }
```

Execute the `compileMontiarc` task with Gradle.
Check that no error occurs.
Moreover, verify that the java class `Status` has been generated to `build/cd2pojo/main/java/com/example/MyTypes/Status.java`.
Note: If an error occurs, try running Gradle's `clean` task before. 

### Using java types
You can use JDK classes from MontiArc models by setting the `useClass2mc` option of the generation task in Gradle's build script:
```kotlin
// The syntax is the same for Kotlin and Groovy build scripts
task.compileMontiarc {
  useClass2Mc.set(true)
}
```

Now use `String` in your MontiArc model [`MyComp`](#executing-the-generation-process):
```
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
```
// Add the following to your build file:
task.compileCd2pojo {
  useClass2Mc.set(true)
}
```

Note that, if you use java types from class diagrams, than you also have to set the `useClass2Mc` option for your MontiArc models.
Else, you will encounter errors.

### Further references 
Applying the MontiArc Gradle plugin provides further benefits, like publishing your MontiArc models and depending on the models of other people.
It also enables fine configuration options.
You can find comprehensive information about the MontiArc plugin under [MontiArc Gradle Plugin] and about the cd2pojo plugin under [Cd2pojo Gradle Plugin].

## Using the command line

### Execute the CLI generation process
The main command line options are:
| Option                       | Explanation                                              |
|------------------------------|----------------------------------------------------------|
| `-mp, --modelpath <dirlist>` | Declare the directory in which your MontiArc models are. |
| `-o, --output <dir>`         | Sets the target path for the generated files (optional). |

To exemplify the usage, lets consider the following example:
Having a project `demo`, create a MontiArc model in `demo/src/montiarc/com/example/MyComp.arc`:
```
package com.example;

component MyComp { }
```

Now execute the CLI tool:
```bash
# Being in the 'demo' directory
java -jar TODONAME --modelpath src/montiarc --output build/montiarc/java
```
Check that this generates the java class `MyComp` at `demo/build/montiarc/java/com/example/MyComp.java`.

### Using class diagrams in MontiArc
To use class diagrams, we first have to export them, as MontiArc has no processing infrastructure for `.cd` files.
Therefore, we must export them to symbolic `.cdsym` files first.
To this end, a generator _cd2pojo_ is provided [here][Nexus cd2pojo Location], having similar requirements as the ma2java generator.
Its main command line options are:
| Option                       | Explanation                                              |
|------------------------------|----------------------------------------------------------|
| `-i, --input <dirlist>`      | Declare the directory in which your class diagram models are. |
| `-c, --checkcocos`           | Verifies the correctness of class diagram models (optional, but recommended). |
| `-o, --output <dir>`         | Sets the target path for the generated java files (optional). |
| `-s, --symboltable <dir>`    | Sets the target path to which symbolic files are exported (optional). |
As we can see, we can use the `--symboltable` option to export the `.cdsym` files that MontiArc can read.
E.g. create a class diagram model at `demo/src/cd2pojo/com/example/MyTypes.cd`:
```
package com.example;

classdiagram MyTypes {
  public enum Status {
    ACTIVE,
    STAND_BY;
  }
}
```

Now execute the generator:
```bash
# Being in the 'demo' directory
java -jar TODO --checkcocos --input src/cd2pojo --symboltable build/cd2pojo/symbols --output build/cd2pojo/java
```
Now check that this created the symbol file `demo/build/cd2pojo/symbols/com/example/MyTypes/Status.cdsym`.
We also generated a java implementation of the class diagram at `demo/build/cd2pojo/java/com/example/MyTypes/Status.java`.
If you do not want to use the generated java implementations, you may omit the `--output` option.

We can now use the class diagram in MontiArc.
To this end, modify [`MyComp`](#execute-the-cli-generation-process):
```
package com.example;

import com.example.MyTypes.Status;

component MyComp(Status initialStatus) { }
```

We can now use the `-path` option of the MontiArc generator to specify directories in which symbol files lay. The symbol files are also allowed to be wrapped in jar files.
Try to execute the generator:
```bash
# Being in the 'demo' directory
java -jar TODONAME --modelpath src/montiarc -path build/cd2pojo/symbols --output build/montiarc/java
```
Verify that no error occurs.

### Using java types
We can use java types form MontiArc models by setting the `-c2mc / --class2mc` command line option of the MontiArc generator.
This will make all java types of the generators class-path available to MontiArc models.
Moreover, one can put jars with the types to be used on the `-path` command line option.
JDK types are available by default.

Let's modify [`MyComp`](#execute-the-cli-generation-process):
```
package com.example;

import java.lang.String;  // TODO: test whether String is automatically imported

component MyComp(String initialStatus) { }
```

Now execute the generator and verify that no error occurs:
```bash
# Being in the 'demo' directory
java -jar TODONAME --modelpath src/montiarc --class2mc --output build/montiarc/java
```

Note that the class diagram generator also has an `-c2mc / class2mc` option to enable the use of java types in class diagram models.

[Gradle]: https://gradle.org/
[IntelliJ Plugins]: https://www.jetbrains.com/help/idea/getting-started-with-gradle.html
[Eclipse Plugin]: https://marketplace.eclipse.org/content/buildship-gradle-integration
[VS Code Plugin]: https://code.visualstudio.com/docs/java/java-build#_gradle
[Java Gradle Plugin]: https://docs.gradle.org/current/userguide/java_plugin.html
[Nexus ma2java Location]: https://nexus.se.rwth-aachen.de/#browse/search=group.raw%3Dmontiarc.generators%20AND%20name.raw%3Dma2java
[Nexus cd2pojo Location]: https://nexus.se.rwth-aachen.de/#browse/search=group.raw%3Dmontiarc.generators%20AND%20name.raw%3Dcd2pojo
