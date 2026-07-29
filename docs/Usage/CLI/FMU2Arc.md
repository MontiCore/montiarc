---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

The FMU2ArcTool offers capabilities for importing Functional Mockup Units that should be used within MontiArc models as if they were MontiArc components.
It provides multiple options that can be used as follows:
```bash
java -jar FMUI2ArcTool.jar -i <dirlist> [-s [<file>]] [-o <dir>]
```

where the arguments are:

| Option                          | Explanation                                                                                                                           |
| ------------------------------- |---------------------------------------------------------------------------------------------------------------------------------------|
| `-i, --input <dirlist>`         | Sets the directories in which the fmu files are.                                                                                      |
| `-o, --output <dir>`            | Sets the target path for the generated files (optional).                                                                              |
| `-s, --symboltable <dir>`       | Serializes and prints the symbol table to standard output or the specified output directory (optional). This creates `.arcsym` files. |

Exemplary usage:


=== "Windows"
    ```cmd
    java -jar FMU2ArcTool.jar ^
      --input src\fmu2arc\ ^
      --output target\fmu2arc\java ^
      --symboltable target\fmu2arc\symbols ^
    ```
=== "Linux/macOS"
    ```bash
    java -jar FMU2ArcTool.jar \
      --input src/fmu2arc/ \
      --output target/fmu2arc/java \
      --symboltable target/fmu2arc/symbols 
    ```
