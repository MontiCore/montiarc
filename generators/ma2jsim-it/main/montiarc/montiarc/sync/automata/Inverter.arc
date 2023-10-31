/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Inverter {

  port in OnOff i;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S [i == OnOff.ON] / {
      o = OnOff.OFF;
    };

    S -> S [i == OnOff.OFF] / {
      o = OnOff.ON;
    };
  }
}
