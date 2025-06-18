/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed;

import montiarc.modes.timed.subcomponents.*;
import montiarc.types.OnOff;

component SimpleTransitionOnEveryMessage {
  port
   in OnOff i,
   out OnOff o;

  mode automaton {
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

    // Because we do not have any conditions, modes will switch with every message or tick.
    Normal -> Inverted;
    Normal -> Inverted i;
    Inverted -> Normal;
    Inverted -> Normal i;
  }
}
