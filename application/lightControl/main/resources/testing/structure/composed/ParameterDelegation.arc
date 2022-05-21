/* (c) https://github.com/MontiCore/monticore */
package testing.structure.composed;

import testing.structure.*;

/**
 * a simple composed component, that instantiates a given subcomponent with a passed on parameter
 */
component ParameterDelegation(int parameterValue) {

  port in int inputValue;
  port out int outputValue;

  Adder a(parameterValue);

  inputValue -> a.inputValue;
  a.outputValue -> outputValue;
}