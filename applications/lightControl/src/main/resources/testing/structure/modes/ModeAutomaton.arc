/* (c) https://github.com/MontiCore/monticore */
package testing.structure.modes;

import testing.structure.*;

/**
 * a component with a simple mode automaton
 */
component ModeAutomaton {

  port in int inputValue;
  port out int outputValue;

  Ones provider;

  mode Duplicating {
    Duplicator d;
    provider.outputValue -> d.inputValue;
    d.outputValue -> outputValue;
  }

  mode Direct {
    provider.outputValue -> outputValue;
  }

  mode automaton {
    initial Duplicating;

    Duplicating -> Direct [inputValue == -1];
  }
}