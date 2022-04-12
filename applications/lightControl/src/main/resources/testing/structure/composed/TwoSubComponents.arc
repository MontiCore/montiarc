/* (c) https://github.com/MontiCore/monticore */
package testing.structure.composed;

import testing.structure.*;

/**
 * a simple composed component, with two subcomponents interacting
 */
component TwoSubComponents {

  port out int outputValue;

  Ones provider;
  Duplicator d;

  provider.outputValue -> d.inputValue;
  d.outputValue -> outputValue;
}