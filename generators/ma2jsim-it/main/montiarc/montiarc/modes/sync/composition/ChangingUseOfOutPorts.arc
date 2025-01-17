/* (c) https://github.com/MontiCore/monticore */
package montiarc.modes.sync.composition;

import montiarc.types.OnOff;

component ChangingUseOfOutPorts {
  port <<sync>> in OnOff i;
  port <<sync>> out OnOff o;

  component WithUse {
    port in OnOff iSub;
    port out OnOff oSub;
    <<sync>> automaton {
      initial state X;
      X -> X / oSub = iSub;;
    }
  }

  component WithoutUse {
    port in OnOff iSub;
  }

  <<sync>> mode automaton {

    initial mode WithConnection {
      WithUse sub;
      i -> sub.iSub;
      sub.oSub -> o;
    }

    mode WithoutConnection {
      WithoutUse sub;
      i -> sub.iSub;
    }

    WithConnection -> WithoutConnection [i == OnOff.OFF];
    WithoutConnection -> WithConnection [i == OnOff.ON];
  }
}
