/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;

component ChangingUseOfOutPorts {
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

  component WithoutUse {
    port in OnOff i;
  }

  <<sync>> mode automaton {

    initial mode WithConnection {
      WithUse sub;
      i -> sub.i;
      sub.o -> o;
    }

    mode WithoutConnection {
      WithoutUse sub;
      i -> sub.i;
    }

    WithConnection -> WithoutConnection [i == OnOff.OFF];
    WithoutConnection -> WithConnection [i == OnOff.ON];
  }
}
