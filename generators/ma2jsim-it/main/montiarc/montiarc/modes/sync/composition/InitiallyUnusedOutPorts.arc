/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;

component InitiallyUnusedOutPorts {
  // The ports are not connected in the view without modes.
  // Only the mode connects the ports
  port in OnOff i;
  port out OnOff o;

  component WithUse {
    port in OnOff i;
    port out OnOff o;
    <<sync>> automaton {
      initial state X;
      X -> X / o = i;;
    }
  }

  <<sync>> mode automaton {

    initial mode WithConnection {
      WithUse sub;

      i -> sub.i;
      sub.o -> o;
    }
  }
}
