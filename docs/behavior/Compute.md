<!-- (c) https://github.com/MontiCore/monticore -->
```montiarc
component FahrenheitToCelsiusAndKelvinConverter {
  port in double fahrenheit,
       out double celsius,
       out double kelvin;

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
When using imperative behavior, one can declare the calculation of outgoing 
port values by using blocks of arbitrary statements.
These statements are declared within the `compute` block.
Moreover, one can define initial outgoing values and other calculations within 
the `init` block, which is especially relevant when using synchronous behavior.

