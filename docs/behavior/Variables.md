<!-- (c) https://github.com/MontiCore/monticore -->

```montiarc
component MovingAverageSmoother {
  port in double roughSignal,
       out double smoothedSignal;

  // Declaring fields to save the most recent values
  double oldestVal = 0.0;
  double olderVal = 0.0;
  double youngerVal = 0.0;
  double youngestVal = 0.0;

  compute {
    double average = (oldestVal + olderVal + youngerVal + youngestVal + roughSignal) / 5.0;
    
    // Updating the history values
    oldestVal = olderVal;
    olderVal = youngerVal;
    youngerVal = youngestVal; 
    youngestVal = roughSignal;

    // Sending the smoothed value through the out port
    smoothedSignal = average;
  }
}
```

Components may contain internal variables to record state that is maintained 
across individual execution steps.
Variables are declared directly within the component body with the syntax 
`<var-type> <var-name> = <initial-value> ;`.
It is mandatory to assign initial values to all variables.
One can also declare multiple variables of the same type within the same 
declaration by separating them with commas:
```montiarc
component MovingAverageSmoother {
  // ...
  double oldestVal = 0.0, olderVal = 0.0, youngerVal = 0.0, youngestVal = 0.0;
  // ...
}
```
