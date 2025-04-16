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

| Option                     | Explanation                                                                                                                                                                     |
| -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `-i, --input <dirlist>`    | Sets the directories in which the MontiArc models are.                                                                                                                          |
| `-o, --output <dir>`       | Sets the target path for the generated files (optional).                                                                                                                        |
| `-hwc <dir>`               | Sets the artifact path for handwritten code customizations (optional).                                                                                                           |
| `-path <dirlist>`          | Sets the artifact path for imported symbols (of other MontiArc or class diagram models) (optional).                                                                             |
| `-pp, --prettyprint <dir>` | Prints the AST of the component models to stdout or the specified directory (optional).                                                                                         |
| `-s, --symboltable <dir>`  | Serializes and prints the symbol table to stdout or the specified output directory (optional). This creates `.arcsym` files.                                                    |
| `-c2mc, --class2mc>`       | Makes Java types from the class path or the symboltable directory available as types in MontiArc models. These can be either `.class` files or bundled within `.jar` archives. |
| `-h, --help`               | Prints the help dialog.                                                                                                                                                         |
| `-v, --version`            | Prints version information.                                                                                                                                                     |

### New

The create command allows you to easily set up a new project.

```bash
montiarc create <name> [-t <templateName>]
```

| Option                         | Description                                                                                                                                                                        |
| ------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `<ProjectName>`                | Name of the newly generated project                                                                                                                                                |
| `-t,--template <templateName>` | Selected template for the project. By default, an empty Gradle project is created. Look [here](https://github.com/MontiCore/montiarc-templates) for the available template options. |


### Run

To run the simulation, the run command can be used.
This runs the simulator for a generated `DeployComp.java` file. Additional parameters
are forwarded to the Component. This command needs Java installed on the
system.

```bash
montiarc run <DeployComp.java> [-cp <arg>]
```

| Option                  | Description                                                              |
| ----------------------- | ------------------------------------------------------------------------ |
| `<FileName>`            | Path to a generated component deploy class                               |
| `-cp,--classpath <arg>` | Additional Java user classes added to the simulation runtime class path. |
