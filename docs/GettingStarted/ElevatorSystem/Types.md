---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->
# Types

Often, primitive types are too restricting for the communication between components or when storing data.
To represent more complex data structures, custom type definitions can be made. For this, we use class diagrams.

## Defining

Type definitions are handled by class diagrams. Just like components, class diagrams have a package and name.
The file ends with the `.cd` ending, and the Gradle plugin expects it to be located in the `src/main/cd2pojo` folder.

For our system, we used a custom `MotorCMD` Enum definition.

=== "Commands.cd"
    ```classdiagram
    package elevator;

    classdiagram Commands {

      public enum MotorCMD {
        UP,
        DOWN,
        STOP;
      }
    }
    ```

## Using

Types can be used for ports, fields, and variables. They can be either fully qualified or imported.
If the type is located in the same package, the import statement can be omitted.

A Fully qualified type contains all the parent packages separated by dots: `elevator.Commands.MotorCMD`.


## Java
In addition to user-defined types, it is possible to use Java types — see
[Java Integration](../../Reference/Types/JavaIntegration.md) for details. This is enabled automatically when using
the `montiarc-jsim`/`montiarc` Gradle plugins — no configuration is needed. When invoking the tool directly from the
command line, pass the corresponding flag explicitly:

=== "CLI"
    ```powershell
    java -cp montiarc.jar montiarc.generator.MA2JSimTool -c2mc [...]
    ```

Inside your MontiArc models, you can then access Java types like any other user-defined type:

=== "ControlStation.arc"
    ```montiarc
    package elevator;
    
    import elevator.Commands.MotorCMD;
    import java.util.TreeSet;
    import java.util.Optional;

    component ControlStation {
      [...]
      TreeSet<int> pendingRequests = TreeSet.TreeSet();
      Optional<int> targetFloor = Optional.empty();
      [...]
    }
    ```

---

A detailed type reference can be found [here](../../Reference/Types/index.md)
