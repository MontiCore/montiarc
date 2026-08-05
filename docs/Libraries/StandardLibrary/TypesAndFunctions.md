---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Types and Functions

These classes can be imported and used in [expressions](../../Reference/Concepts/Expressions/index.md).

---

### montiarc.lang.Console
A class containing static methods for interacting with the console.

*It cannot be instantiated.*

| Method                                  | Description                                                      |
| --------------------------------------- | ---------------------------------------------------------------- |
| **`static void printLn(String value)`** | Prints the content of `value` to the `stdout` (i.e. the console) |

---

### montiarc.lang.Duration

Used to specify durations.

| Method                                  | Description                                                                                                    |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| **`Duration add(Duration other)`**      | Returns a new duration with other added to self                                                                |
| **`Duration subtract(Duration other)`** | Returns a new duration with other subtracted from self. If other is greater then self, returns a duration of 0 |

??? info "Instantiation Methods"
    | Creates a new duration with the specified length                                                    |
    | --------------------------------------------------------------------------------------------------- |
    | **`static Duration ofMilliseconds(long milliseconds)`**                                             |
    | **`static Duration ofSeconds(long seconds)`**                                                       |
    | **`static Duration ofSeconds(long seconds, long milliseconds);`**                                   |
    | **`static Duration ofMinutes(long minutes);`**                                                      |
    | **`static Duration ofMinutes(long minutes, long seconds);`**                                        |
    | **`static Duration ofMinutes(long minutes, long seconds, long milliseconds);`**                     |
    | **`static Duration ofHours(long hours);`**                                                          |
    | **`static Duration ofHours(long hours, long minutes);`**                                            |
    | **`static Duration ofHours(long hours, long minutes, long seconds);`**                              |
    | **`static Duration ofHours(long hours, long minutes, long seconds, long milliseconds);`**           |
    | **`static Duration ofDays(long days);`**                                                            |
    | **`static Duration ofDays(long days, long hours);`**                                                |
    | **`static Duration ofDays(long days, long hours, long minutes);`**                                  |
    | **`static Duration ofDays(long days, long hours, long minutes, long seconds);`**                    |
    | **`static Duration ofDays(long days, long hours, long minutes, long seconds, long milliseconds);`** |

---

### montiarc.lang.Signal
Objects of this class represent a signal. Can be used for event ports that where only the event is relevant but not the value.

| Method                    | Description             |
| ------------------------- | ----------------------- |
| **`static Signal get()`** | Returns a Signal object |

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

### montiarc.lang.Timer

A timer object for interacting with time.

| Method                                       | Description                                               |
| -------------------------------------------- | --------------------------------------------------------- |
| **`static Timer start(Duration duration);`** | Starts a new timer. Returns the running timer object      |
| **`boolean completed();`**                   | Returns `true` if the timer duration has elapsed          |
| **`Duration startedAgo();`**                 | Return the duration of how long ago the timer was started |

---