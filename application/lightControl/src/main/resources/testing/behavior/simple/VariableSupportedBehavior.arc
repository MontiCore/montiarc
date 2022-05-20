/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.simple;

/**
 * tests whether behavior can be influenced by persistent variables
 * (which add state to the component)
 */
component VariableSupportedBehavior {

  port in int inputValue;
  port out int outputValue;

  int fieldValue = 0;

  compute {
    fieldValue += inputValue;
    outputValue = inputValue + fieldValue;
  }
}