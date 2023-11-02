<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
package com.example;

// Top-level component type definition
component MyComp {
  
  // Nested component type definition
  component Foo {
    // ...
  }
  
}
```
Component types can be defined
* at the top level of a model file or
* within the body of other components as *nested component types*.
  However, nested component types can only be used within the context in which they are defined.
  In the example above, component type `Foo` could only be used within `MyComp`.


## Complete syntax
```
component <name> (<type-parameters>) (<configuration-parameters>) (extends <parent-component>) {
  // Component body
}
```
Example:
```
component AgeCategorizer <K, V extends Person> (double classSize, Rule<K, V> rule)
  extends BiProcessor<K, V> (rule) {
  // Component body
}
```
