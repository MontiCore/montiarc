/* (c) https://github.com/MontiCore/monticore */
package testing.modes.reactions;

import testing.structure.*;
import openmodeautomata.runtime.*;

/**
 * Has a reaction that changes the order of both subcomponents in the architecture.
 * (Would be simpler to achieve using usual modes, but for testing purposes we use reactions).
 */
component ChangeConnectors {

  port in int inputValue;
  port out int outputValue;

  Adder adder(5);
  Duplicator duplicator;

  inputValue -> adder.inputValue;
  adder.outputValue -> duplicator.inputValue;
  duplicator.outputValue -> outputValue;

  mode AddFirst, AddFirst2, AddSecond {}

  mode automaton {
    initial AddFirst;

    AddFirst -> AddFirst2;

    // transitions that change connectors
    AddFirst2 -> AddSecond / {
      SourcePort a = getInputPort("inputValue");
      TargetPort b = getSubcomponent("adder").getInputPort("inputValue");
      SourcePort c = getSubcomponent("adder").getOutputPort("outputValue");
      TargetPort d = getSubcomponent("duplicator").getInputPort("inputValue");
      SourcePort e = getSubcomponent("duplicator").getOutputPort("outputValue");
      TargetPort f = getOutputPort("outputValue");
      disconnectAll();
      connectPorts(a, d);
      connectPorts(e, b);
      connectPorts(c, f);
    };
  }
}