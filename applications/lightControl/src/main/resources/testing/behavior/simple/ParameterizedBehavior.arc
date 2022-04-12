/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.simple;

/**
 * tests whether behavior can be influenced by parameters
 */
component ParameterizedBehavior(int parameterValue) {

  port in int inputValue;
  port out int outputValue;

  compute {
    outputValue = inputValue + parameterValue;
  }
}