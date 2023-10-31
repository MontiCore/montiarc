/* (c) https://github.com/MontiCore/monticore */
package montiarc.timed.automata;

import montiarc.types.OnOff;

component Inverter {

  port in OnOff i;
  port out OnOff o;

  <<timed>> automaton {
    initial state S;

    S -> S [i == OnOff.ON] i / {
      o = OnOff.OFF;
    };

    S -> S [i == OnOff.OFF] i / {
      o = OnOff.ON;
    };
  }
}
