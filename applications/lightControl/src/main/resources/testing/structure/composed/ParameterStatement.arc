/* (c) https://github.com/MontiCore/monticore */
package testing.structure.composed;

import testing.structure.*;

/**
 * a simple composed component, that instantiates a given subcomponent with a certain parameter
 */
component ParameterStatement {

  port in int inputValue;
  port out int outputValue;

  Adder a(4);

  inputValue -> a.inputValue;
  a.outputValue -> outputValue;
}