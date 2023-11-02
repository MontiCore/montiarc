<!-- (c) https://github.com/MontiCore/monticore -->
The MA2JavaTool offers capabilities for processing MontiArc component 
models via the command line.
It provides multiple options that can be used as follows:
```bash
java -jar MA2JavaTool.jar [-h] -mp <dirlist> [-path <p>] [-pp [<file>]] [-s [<file>]] [-o <dir>]
```

where the arguments are:

| Option                       | Explanation                                                                                                                                                                      |
|------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-mp, --modelpath <dirlist>` | Sets the directories in which the MontiArc models are.                                                                                                                           |
| `-o, --output <dir>`         | Sets the target path for the generated files (optional).                                                                                                                         |
| `-hwc <dir>`                 | Sets the artifact path for handwritten code customizations(optional).                                                                                                            |
| `-path <dirlist>`            | Sets the artifact path for imported symbols (of other MontiArc or class diagram models) (optional).                                                                              |
| `-pp, --prettyprint <dir>`   | Prints the AST of the component models to stdout or the specified directory (optional).                                                                                          |
| `-s, --symboltable <dir>`    | Serializes and prints the symbol table to stdout or the specified output directory (optional). This creates `.arcsym` files.                                                     |
| `-c2mc, --class2mc>`         | Makes Java types from the class path or the symboltable directory available as types in MontiArc models. These can be either `.class` files, or bundled within `.jar` archives. |
| `-h, --help`                 | Prints the help dialog.                                                                                                                                                          |
| `-v, --version`              | Prints version information.                                                                                                                                                      |

Exemplary usage:

On Linux/MacOS:
```bash
java -jar MA2JavaTool.jar \
  --modelpath src/montiarc \
  -hwc src/java \
  --output target/montiarc/java \
  --symboltable target/montiarc/symbols \
  -c2mc
```
On Windows:
```cmd
java -jar MA2JavaTool.jar ^
  --modelpath src\montiarc ^
  -hwc src\java ^
  --output target\montiarc\java ^
  --symboltable target\montiarc\symbols ^
  -c2mc
```
