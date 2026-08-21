# Contributing

This guide is for developers of MontiArc, or those who want to become one.

For a general introduction into the MontiArc language read the [Documentation](https://monticore.github.io/montiarc).

MontiArc is built using the [MontiCore](https://www.monticore.de/) language workbench.

This guide comprises the following steps:

- [Setting up the Development Environment](#setting-up-the-development-environment)
- [Documentation](#documentation)
- [Making a Pull Request](#making-a-pull-request)
- [Debug Support](#debug-support)

## Setting up the Development Environment

### Setup

#### Prerequisites 
- Git (for checking out the project) and [Git-LFS](https://git-lfs.com/)
- Gradle 8.14.4 (for building the project)
- Java 21 (for building and executing the project)

#### Cloning the Project
This project uses Git as its version control system. A repository can be cloned with:

```bash
git clone <link to this Git repository>
```

#### Using an IDE

We recommend using an IDE (e.g. IntelliJ) for developing MontiArc.

#### Building the Project

To build the project run:

```bash
gradle build
```

This builds all subprojects and executes all test.

> Please note that `gradle build` on the topmost folder builds the whole project. 
> However, each subproject contains an individual build file. 
> Executing the build command on a subproject builds everything needed for that 
> project and then builds the project. See the [Gradle Website](https://gradle.org/) 
> for more information about the Gradle build tool.

### Documentation

#### Prerequisites
- Python
- MkDocs Material (can be installed with `pip install mkdocs mkdocs-material`)

#### View Changes
Run a development server with `mkdocs serve` and open `http://127.0.0.1:8000/montiarc` in your browser

## Project Structure

The project can generally be divided into four parts:
1. **Frontend**: Located in the `languages` folder this contains everything related to parsing and processing of MontiArc models. That is parsers to create abstract
syntax trees (ASTs) from textual models, infrastructure to create the symbol 
table, context condition checks, transformations, visitors, and a command line 
tool that puts everything together.
2. **Backend**: Located in the `generators` folder this contains code for translating MontiArc models into general-purpose languages.
3. **Applications**: Located in the `applications` folder are example projects that use MontiArc.
4. **Documentation**: The `docs` folder contain all pages of the documentation.

## Debug support
The Gradle plugins `cd2pojo` and `montiarc-jsim` offer configuration options 
to facilitate the debugging of generator executions in integration and 
application projects: 
All [`CD2PojoCompile`](tooling%2Fgradle-plugins%2Fcd2pojo%2Fmain%2Fkotlin%2FCD2PojoCompile.kt) 
and [`MontiArcCompile`](tooling%2Fgradle-plugins%2Fma2jsim%2Fmain%2Fkotlin%2FMontiArcCompile.kt) 
tasks expose a boolean property called `debugTask`. When set to `true`, the 
corresponding task will wait for a remote debugger to connect on port `5005` 
by default. This port can be altered using the `debugPort` string property. 
There are two ways to configure these debugging options:

1. In the build script of the project that applies the respective plugin:
   ```kotlin
   tasks.compileMontiarc {
     debugTask.set(true)    // Default value is false
     debugPort.set("3003")  // Default value is "5005"
   }
   ```
2. Via a command line option when executing Gradle
   ```bash
   ./gradlew :applications:factory:compileMontiarc --debugTask
   ```
   Note that configuring which port to use, again, is optional. The default port is 5005.
   ```bash
   ./gradlew :applications:factory:compileMontiarc --debugTask --debugPort=3003
   ```

## Making a Pull Request

When creating a change there a few thing to consider.

1. Follow the established coding style and guidelines.
2. Make sure you added sufficient tests
3. Commit your changes to a newly created branch.
4. Create a PR to the `dev` branch of the project.

## Making a Release

Making a MontiArc release is a four-step process.

1. Upgrade all MontiCore dependency to their respective stable version and commit to develop. Files to edit are:
   - [build-logic/settings.gradle.kts](./build-logic/settings.gradle.kts)
   - [settings.gradle.kts](settings.gradle.kts)
   - [tooling/language-server/settings.gradle.kts](tooling/language-server/settings.gradle.kts)
2. Release new version by removing `-SNAPSHOT` everywhere. Files to edit are (best use search+replace)
  - [build-logic/settings.gradle.kts](build-logic/settings.gradle.kts)
  - [build-logic/src/main/kotlin/montiarc/build/BuildConstants.kt](build-logic/src/main/kotlin/montiarc/build/BuildConstants.kt)
  - [docs/GettingStarted/Editor.md](docs/GettingStarted/Editor.md)
  - [docs/GettingStarted/HelloWorld.md](docs/GettingStarted/HelloWorld.md)
  - [docs/GettingStarted/Setup.md](docs/GettingStarted/Setup.md)
  - [docs/Libraries/index.md](docs/Libraries/index.md)
  - [settings.gradle.kts](settings.gradle.kts)
  - [tooling/language-server/example/build.gradle.kts](tooling/language-server/example/build.gradle.kts)
  - [tooling/language-server/settings.gradle.kts](tooling/language-server/settings.gradle.kts)
  - [templates/*/build.gradle.kts](templates)
3. Push a tag to GitHub named `7.x.x`
4. Release new snapshot by increasing the version number and adding `-SNAPSHOT` back again. Files to edit are (best use search+replace):
   - [build-logic/settings.gradle.kts](build-logic/settings.gradle.kts)
   - [build-logic/src/main/kotlin/montiarc/build/BuildConstants.kt](build-logic/src/main/kotlin/montiarc/build/BuildConstants.kt)
   - [docs/GettingStarted/Editor.md](docs/GettingStarted/Editor.md)
   - [docs/GettingStarted/HelloWorld.md](docs/GettingStarted/HelloWorld.md)
   - [docs/GettingStarted/Setup.md](docs/GettingStarted/Setup.md)
   - [docs/Libraries/index.md](docs/Libraries/index.md)
   - [settings.gradle.kts](settings.gradle.kts)
   - [tooling/language-server/example/build.gradle.kts](tooling/language-server/example/build.gradle.kts)
   - [tooling/language-server/settings.gradle.kts](tooling/language-server/settings.gradle.kts)
   - [templates/*/build.gradle.kts](templates)

## Making a point release

1. Checkout the specific tag you want to create a point release from.
2. Create a local branch at that tag
3. Follow step 4 of making a release and only increase the last digit (without adding the snapshot tag)
4. Publish to our nexus from your local device
5. Push a tag to GitHub named `7.x.y`

You can then delete your local branch again.

## Further Information

* [Project root: MontiArc @GitHub](https://github.com/MontiCore/montiarc)
* [**MontiArc - Documentation**](https://monticore.github.io/montiarc)
* [MontiCore - Documentation](https://www.monticore.de/)
* [MontiCore - List of languages](https://github.com/MontiCore/monticore/blob/HEAD/docs/Languages.md)
* [MontiCore - Grammar Library](https://github.com/MontiCore/monticore/blob/HEAD/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [MontiCore - Best Practices](https://github.com/MontiCore/monticore/blob/HEAD/docs/BestPractices.md)
* [Publications about MBSE, MontiArc, and MontiCore](https://www.se-rwth.de/publications/)
