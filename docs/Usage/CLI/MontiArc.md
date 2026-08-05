<!-- (c) https://github.com/MontiCore/monticore -->
The MontiArc tool offers capabilities for processing MontiArc component 
models via the command line.

To get a brief introduction on how to use this command, look at the [HelloWorld](../../GettingStarted/HelloWorld.md) guide.

### Build
The main and default command of MontiArc is responsible for parsing, analyzing, and building the simulator files.

```bash
montiarc [build] [-h] -i <dirlist> [-path <p>] [-pp [<file>]] [-s [<file>]] [-o <dir>]
```

Where the arguments are:

| Option                            | Explanation                                                                       |
|-----------------------------------|-----------------------------------------------------------------------------------|
| `-h, --help`                      | Prints the help dialog                                                            |
| `-v, --version`                   | Prints version information                                                        |
| `-i, --input <dirlist>`           | Parses alls MontiArc models from the specified files or directories (recursively) |
| `-o, --output <dir>`              | Generates java code to the specified directory                                    |
| `-path, --path <dirlist>`         | Sets the artifact path for imported symbols, space separated                      |
| `-hwc, --handwritten-code <dir>`  | Sets the artifact path for handwritten code, space separated                      |
| `-pp, --prettyprint <dir>`        | Prints the models to stdout or the specified directory (optional)                 |
| `-s, --symboltable <dir>`         | Serializes the symbol table of the given artifacts to the specified directory     |
| `-r, --report <dir>`              | Prints reports of the artifact to the specified directory                         |
| `-c2mc, --class2mc`               | Enables importing java symbols from the java runtime environment                  |
| `-novar, --no-variability-checks` | Disable the analysis of variable components for better performance                |
| `-d, --debug`                     | Enables verbose logging with debug-level information                              |
| `-t, --trace`                     | Enables verbose logging with trace-level information                              |

### New

The create command allows you to easily set up a new project.

```bash
montiarc create <name> [-t <templateName>]
```

| Option                         | Description                                                                                                                                                                                     |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `<ProjectName>`                | Name of the newly generated project                                                                                                                                                             |
| `-t,--template <templateName>` | Selected template for the project. By default, an empty Gradle project is created. Look [here](https://github.com/MontiCore/montiarc/releases/tag/snapshot) for the available template options. |


### Run

To run the simulation, the run command can be used.
This runs the simulator for a generated `DeployComp.java` file. Additional parameters
are forwarded to the Component. This command needs Java installed on the
system.

```bash
montiarc run <CompName.arc> [-cp <arg>]
```

| Option                  | Description                                                              |
| ----------------------- | ------------------------------------------------------------------------ |
| `<FileName>`            | Path to a generated component deploy class                               |
| `-cp,--classpath <arg>` | Additional Java user classes added to the simulation runtime class path. |
