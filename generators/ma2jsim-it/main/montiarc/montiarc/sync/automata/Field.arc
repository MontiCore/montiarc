/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Field {

  OnOff v = montiarc.types.OnOff.OFF;

  port in OnOff i;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S [i == OnOff.ON] / {
      o = v;
      v = i;
    };

    S -> S [i == OnOff.OFF] / {
      o = v;
      v = i;
    };
  }
}
