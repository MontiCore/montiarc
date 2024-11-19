/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed;

import montiarc.modes.timed.subcomponents.*;
import montiarc.types.OnOff;

component SimpleTransition {
  port
   <<timed>> in OnOff i,
   <<timed>> out OnOff o;

  <<timed>> mode automaton {
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

    // Because we do not have any conditions, modes will switch with every message and tick.
    Normal -> Inverted i;
    Normal -> Inverted Tick;
    Inverted -> Normal i;
    Inverted -> Normal Tick;
  }
}
