/* (c) https://github.com/MontiCore/monticore */
package testing.structure.composed;

import testing.structure.*;

/**
 * a simple composed component,
 * that additionally to "OutputPortForward" contains a second port-forward
 */
component InputPortForward {

  port in int inputValue;
  port out int outputValue;

  Duplicator d;

  inputValue -> d.inputValue;
  d.outputValue -> outputValue;
}