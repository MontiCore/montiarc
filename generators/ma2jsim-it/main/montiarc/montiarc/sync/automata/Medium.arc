/* (c) https://github.com/MontiCore/monticore */
package montiarc.sync.automata;

import montiarc.types.OnOff;

component Medium {

  port in OnOff i;
  port out OnOff o;

  <<sync>> automaton {
    initial state S;

    S -> S / {
      o = i;
    };
  }
}
