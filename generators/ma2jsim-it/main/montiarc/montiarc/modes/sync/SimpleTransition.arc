/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync;

import montiarc.modes.sync.subcomponents.*;
import montiarc.types.OnOff;

component SimpleTransition {
  port
   <<sync>> in OnOff i,
   <<sync>> out OnOff o;

  <<sync>> mode automaton {
    initial mode Normal {
      Forwarder compNormal;
      i -> compNormal.i;
      compNormal.o -> o;
    }

    mode Inverted {
      Inverter compInverted;
      i -> compInverted.i;
      compInverted.o -> o;
    }

    // Because we do not have any conditions, modes will switch with every tick.
    Normal -> Inverted;
    Inverted -> Normal;
  }
}
