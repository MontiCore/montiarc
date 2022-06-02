/* (c) https://github.com/MontiCore/monticore */
package testing.modes.reactions;

import testing.structure.*;
import openmodeautomata.runtime.*;
import java.util.Map;

/**
 * an extension to AddSubcomponents, but this one removes its subcomponents again
 */
component RemoveSubcomponents(Map<Integer, SubcomponentInstance> emptyMap) {

  port in int inputValue;
  port out int outputValue;

  Ones provider;
  provider.outputValue -> outputValue;

  Map<Integer, SubcomponentInstance> adders = emptyMap;

  mode OnlyState {}

  mode automaton {
    initial OnlyState;

    // transitions that removes subcomponents
    OnlyState -> OnlyState [adders.containsKey(Integer.valueOf(inputValue))] / {
      // reference to the subcomponent to delete, stored in the map
      SubcomponentInstance adder;
      // typecheck does not support autoboxing yet
      adder = adders.remove(Integer.valueOf(inputValue));
      for(Connector outputConnector: getConnectors(adder.getOutputPort("outputValue"))){
        connectAnyways(getConnector(adder.getInputPort("inputValue")).getSource(), outputConnector.getTarget());
      }
      delete(adder);
    };

    // same transition as in AddSubcomponents
    OnlyState -> OnlyState [inputValue != 0] / {
      SubcomponentInstance adder = Adder(inputValue);
      TargetPort mainOutput = getOutputPort("outputValue");
      SourcePort oldSource = connectAnyways(adder.getOutputPort("outputValue"), mainOutput);
      connectPorts(oldSource, adder.getInputPort("inputValue"));
      // only difference is adding the subcomponent to the map
      adders.put(Integer.valueOf(inputValue), adder);
    };
  }
}