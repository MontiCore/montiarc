/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;
import montiarc.sync.automata.Parameter;

component SourceEncapsulation {
  port in OnOff i;
  port out OnOff o;

  mode automaton {

    initial mode S1 {
      Parameter sub(OnOff.OFF);  // Produces 'OFF' for every tick
      sub.o -> o;
    }

    mode S2 {
      Parameter sub(OnOff.ON);  // Produces 'ON' for every tick
      sub.o -> o;
    }

    S1 -> S2;
    S2 -> S1;
  }
}
