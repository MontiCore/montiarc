---
hide:
  - toc
---
<!-- (c) https://github.com/MontiCore/monticore -->

# Parameters

Components can carry _configuration parameters_. These allow us to make component behaviors configurable and facilitate reuse.
When defining a component type, the definition of configuration parameters, wrapped in parentheses, directly follows the name of the component.
Multiple parameters are separated by commas.
One can also define default values for parameters that then do not need to be bound during instantiation.
Note, however, that once a parameter has a default value, all following parameters must have default values, too.

Example:
```montiarc
component LinearScalar(double factor, double offset = 0.0) {
  port in double baseValue,
       out double scaledValue;

  compute {
    scaledValue = factor * baseValue + offset;
  }
}
```


## Instantiation of components with configuration parameters
When components with parameters are used during [decomposition](./Decomposition.md), values have to be assigned to these.
These values can be any [expression](../Concepts/Expressions/index.md) and can contain parameters of the enclosing component, too.

```montiarc
component MeasureNet {
  LinearScalar scaleAndAdd(3.0, 10);
  LinearScalar justScale(2.0);  // justScale's offset is implicitly assigned 0.0, the default value

  // The rest of the component is omitted here.
}
```