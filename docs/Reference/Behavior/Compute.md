---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Compute

When using imperative behavior, one can declare the calculation of outgoing 
port values by using blocks of arbitrary statements.
These statements are declared within the `compute` block.
Moreover, one can define initial outgoing values and other calculations within 
the `init` block, which is especially relevant when using [delayed](../Component/Interfaces.md#delayed-ports) interfaces.

```montiarc
component FahrenheitToCelsiusAndKelvinConverter {
  port sync in double fahrenheit,
       <<delayed>> sync out double celsius,
       <<delayed>> sync out double kelvin;

  init {
    kelvin = 0;
    celsius = -273.15;  // Equivalent to 0 °K
  }

  compute {
    celsius = (fahrenheit - 32) * 5.0 / 9.0;
    kelvin = (fahrenheit + 495.67) * 5.0 / 9.0;
  }
}
```

!!! warning
    Compute blocks can only read from synchronous ports, but write to all port regardless of the timing.
