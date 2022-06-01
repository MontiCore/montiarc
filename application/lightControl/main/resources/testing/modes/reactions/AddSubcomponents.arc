/* (c) https://github.com/MontiCore/monticore */
package testing.modes.reactions;

import testing.structure.*;
import openmodeautomata.runtime.*;

/**
 * a component with a mode automaton, whose transition adds subcomponents
 */
component AddSubcomponents {

  port in int inputValue;
  port out int outputValue;

  Ones provider;
  provider.outputValue -> outputValue;

  mode OnlyState {}

  mode automaton {
    initial OnlyState;

    OnlyState -> OnlyState [inputValue != 0] / {
      SubcomponentInstance adder = Adder(inputValue);
      TargetPort mainOutput = getOutputPort("outputValue");
      SourcePort oldSource = connectAnyways(adder.getOutputPort("outputValue"), mainOutput);
      connectPorts(oldSource, adder.getInputPort("inputValue"));
    };
  }
}