/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.composition;

import montiarc.types.OnOff;

component InitiallyUnusedOutPorts {
  // The ports are not connected in the view without modes.
  // Only the mode connects the ports
  port in OnOff i;
  port out OnOff o;

  component WithUse {
    port in OnOff i;
    port out OnOff o;
    <<timed>> automaton {
      initial state X;
      X -> X i / o = i;;
    }
  }

  <<timed>> mode automaton {

    initial mode WithConnection {
      WithUse sub;

      i -> sub.i;
      sub.o -> o;
    }
  }
}
