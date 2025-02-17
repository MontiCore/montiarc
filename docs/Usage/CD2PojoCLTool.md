<!-- (c) https://github.com/MontiCore/monticore -->
The CD2PojoTool offers capabilities for processing class diagram models that later should be used within MontiArc models.
It provides multiple options that can be used as follows:
```bash
java -jar CD2PojoTool.jar [-h] -i <dirlist> [-path <p>] [-pp [<file>]] [-s [<file>]] [-o <dir>]
```

where the arguments are:

| Option                          | Explanation                                                                                                                                                                           |
|---------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `-i, --input <dirlist>`         | Sets the directories in which the class diagram models are.                                                                                                                           |
| `-o, --output <dir>`            | Sets the target path for the generated files (optional).                                                                                                                              |
| `-hwc, --handwrittencode <dir>` | Sets the artifact path for handwritten code customizations(optional).                                                                                                                 |
| `-path <dirlist>`               | Sets the artifact path for imported symbols (of other class diagram models) (optional).                                                                                               |
| `-pp, --prettyprint <dir>`      | Prints the AST of the class diagram models to stdout or the specified directory (optional).                                                                                           |
| `-s, --symboltable <dir>`       | Serializes and prints the symbol table to stdout or the specified output directory (optional). This creates `.cdcsym` files.                                                          |
| `-c2mc, --class2mc>`            | Makes Java types from the class path or the symboltable directory available as types in class diagram models. These can be either `.class` files, or bundled within `.jar` archives. |
| `-h, --help`                    | Prints the help dialog.                                                                                                                                                               |
| `-v, --version`                 | Prints version information.                                                                                                                                                           |

Exemplary usage:


With Linux/MacOS:
```bash
java -jar CD2PojoTool.jar \
  --input src/cd2pojo \
  -hwc src/java \
  --output target/cd2pojo/java \
  --symboltable target/cd2pojo/symbols \
  -c2mc
```

With Windows:
```cmd
java -jar CD2PojoTool.jar ^
  --input src\cd2pojo ^
  -hwc src\java ^
  --output target\cd2pojo\java ^
  --symboltable target\cd2pojo\symbols ^
  -c2mc
```