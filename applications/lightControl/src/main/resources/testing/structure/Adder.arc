/* (c) https://github.com/MontiCore/monticore */
package testing.structure;

/**
 * adds its values together
 */
component Adder(int parameterValue) {

  port in int inputValue;
  port out int outputValue;

  compute {
    outputValue = inputValue + parameterValue;
  }
}