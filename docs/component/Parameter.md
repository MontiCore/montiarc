<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component LinearScalar(double factor, double offset = 0.0) {
  port in double baseValue,
       out double scaledValue;

  compute {
    scaledValue = factor * baseValue + offset;
  }
}
```

Components can carry _configuration parameters_.
Values are assigned to them at instantiation and can be used to influence component behavior by reading their values.
When defining a component type, the definition of configuration parameters, wrapped in parenthesis, directly follow the name of the component.
Multiple parameters are separated by commas.
One can also define default values for parameters that then do not need to be bound during instantiation.
Note however, that once a parameter has a default value, all following parameters must have default values, too.

## Instantiation of components with configuration parameters
```montiarc
component MeasureNet {
  LinearScalar scaleAndAdd(3.0, 10);
  LinearScalar justScale(2.0);  // justScale's offset is implicitly assigned 0.0, the default value

  // The rest of the component is omitted here.
}
```