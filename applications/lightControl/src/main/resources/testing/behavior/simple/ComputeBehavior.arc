/* (c) https://github.com/MontiCore/monticore */
package testing.behavior.simple;

/**
 * tests whether behavior can be modeled with a simple compute
 */
component ComputeBehavior {

  port in int inputValue;
  port out int outputValue;

  compute {
    outputValue = inputValue;
  }
}