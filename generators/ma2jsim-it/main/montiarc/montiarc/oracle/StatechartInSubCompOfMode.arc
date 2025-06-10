/* (c) https://github.com/MontiCore/monticore */
package montiarc.oracle;

import montiarc.types.OnOff;

/** Test whether we can address the sub component in a mode */
component StatechartInSubCompOfMode {
  port sync in OnOff i;
  port out int o;

  mode automaton {

    initial mode S1 {
      WithStatechart sub;
      i -> sub.i;
      sub.o -> o;
    }
  }
}
