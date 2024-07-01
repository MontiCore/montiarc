/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.timed.composition;

import montiarc.types.OnOff;

component ChangingUseOfOutPorts {
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

  component WithoutUse {
    port in OnOff i;
  }

  <<timed>> mode automaton {

    initial mode WithConnection {
      WithUse sub;
      i -> sub.i;
      sub.o -> o;
    }

    mode WithoutConnection {
      WithoutUse sub;
      i -> sub.i;
    }

    WithConnection -> WithoutConnection [i == OnOff.OFF] i;
    WithoutConnection -> WithConnection [i == OnOff.ON] i;
  }
}
