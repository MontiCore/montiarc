<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component Dummy {
  int myIntField = 0;
  long myLongField = 0;

  float myFloatField = 0.0f;
  double myDoubleField = 0.0;

  boolean myBoolField = false;
  char myCharField = 'a';
}
```

MontiArc provides the following primitive data types:
## Integer numbers
<!-- Byte and Short are excluded for now as they are pretty useless:
     One can not assign any constant values to them at the moment. -->
<!--* `byte` with values between `-128` and `127`-->
<!--* `short` with values between `-2¹⁵` and `2¹⁵ - 1`-->
* `int` with values between `-2³¹` and `2³¹ - 1`
* `long` with values between `-2⁶³` and `2⁶³ - 1`

Normally, integer literals are of the type `int`.
However, it is also possible to create literals that are explicitly of the type `long` by adding an `l` or `L` as a suffix: `42l` or `42L`. (`L` is the recommended suffix, as `l` is similar to `1`).
Values of type `int` can be assigned to entities of the `long`, `double`, `float`, and `char` type.

## Floating point numbers
* `float` is a single-precision 32-bit IEEE 754 floating point
* `double` is a double-precision 64-bit IEEE 754 floating point

The behavior of floating point numbers (e.g. rounding after math operations) follows the behavior of [Java's floating point] numbers.
Normally, floating point literals are of the type `double`.
However, it is also possible to create literals of the type `float` by adding an `f` or `F` as a suffix: `2.0f` or `2.0F`. 
Values of the type `float` can be assigned to entities of the `double` type.

## Boolean values
`boolean` data can have two different values: `true` and `false`

## Character values
`char` is a 16-bit Unicode character.
It is possible to define a character value by its unicode code point. To this end, the hexadecimal unicode code is following the unicode escape `\u`: `char foo = '\u0F23'`.
Moreover, MontiArc features the [same escape codes as Java][java escape codes] for commonly used control characters such as `\n` for new line.
Character values can be assigned to entities of the `int`, `long`, `double`, and `float` type.

## Void
While the `void` type is not usable as type of values, it is recognized as the return type of methods that do not return anything:
```montiarc
automaton {
  // ...
  A -> B / { executeVoidMethod(); };
}
```
