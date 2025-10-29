/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;

component InitiallyUnusedOutPorts {
  // The ports are not connected in the view without modes.
  // Only the mode connects the ports
  port sync in OnOff i;
  port sync out OnOff o;

  component WithUse {
    port sync in OnOff i;
    port sync out OnOff o;
    automaton {
      initial state X;
      X -> X / o = i;
    }
  }

  mode automaton {

    initial mode WithConnection {
      WithUse sub;

      i -> sub.i;
      sub.o -> o;
    }
  }
}
