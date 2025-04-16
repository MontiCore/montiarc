---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Functions

These classes can be imported and used in [expressions](../Reference/Concepts/Expressions.md#method-invocation).

---

### montiarc.lang.Console
A class containing static methods for interacting with the console.

*It cannot be instantiated.*

| Method                                  | Description                                                      |
| --------------------------------------- | ---------------------------------------------------------------- |
| **`static void printLn(String value)`** | Prints the content of `value` to the `stdout` (i.e. the console) |

---

### montiarc.lang.Simulation

A class containing static methods for interacting with the simulation environment.

*It cannot be instantiated.* 

| Method                   | Description                                                         |
| ------------------------ | ------------------------------------------------------------------- |
| **`static void stop()`** | Stops the simulation after the current tick has been fully computed |

!!! warning
    The simulation class is only available when using the MontiArc Java simulator

---
